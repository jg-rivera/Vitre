package local.vitre.desktop.record.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.POIDocument;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.DocumentImport;
import local.vitre.desktop.record.patch.ContextManager;
import local.vitre.desktop.task.TaskHandler;
import local.vitre.desktop.util.Utils;

public class WorkbookDocument implements Flushable {

	private ClassRecord record;
	private SchemaReader reader;
	private Recognizer schema;
	private RecordConfiguration config;

	private Workbook workbook;

	private String name;
	private String path;
	private long modTimestamp;
	private String lastModifiedDate;
	private String contextFileName;

	private Format format;
	private boolean loaded;
	private boolean hasRepresentation;

	public WorkbookDocument(String path) {
		this.path = path;
	}

	public boolean prepare(DocumentImport imp) {

		try {
			File file = new File(path);
			FileInputStream excelFile = new FileInputStream(file);
			name = file.getName();
			modTimestamp = file.lastModified();
			contextFileName = imp.getContextFilePath();
			lastModifiedDate = Vitre.dateFormat.format(modTimestamp);

			Log.info("WORKBOOK", "Creating workbook document for " + name);
			Log.info("WORKBOOK", "Loading workbook file from " + file.getAbsolutePath());

			reader = Vitre.schemaReader;
			format = createWorkbook(excelFile, Format.getFormat(Utils.getExtension(name)));
			loaded = true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Log.fine("WORKBOOK", "Format of workbook file: " + format.name());
		Log.fine("WORKBOOK", "Available sheets in this workbook: " + getSheetCount());
		Log.fine("WORKBOOK", "Sheets: " + getSheetNames().toString());

		try {
			if (!imp.isCached()) {
				Log.fine("WORKBOOK", "Determing schema for non-cached import.");
				schema = reader.determineSchema(this);
			} else {
				schema = reader.getSchema(imp.getSchemaName());
				if (schema == null)
					schema = reader.determineSchema(this);
				Log.fine("WORKBOOK", "Read schema from cached import: " + schema.getName());
			}
		} catch (IllegalArgumentException e) {
			return false;
		}

		config = schema.getConfig(this);
		config.preview();
		return true;
	}

	private Format createWorkbook(FileInputStream s, Format format) throws IOException {
		switch (format) {
		case XLS:
			workbook = new HSSFWorkbook(s);
			Log.fine("WORKBOOK", "Using HSSF Workbook structure.");
			return Format.XLS;
		case XLSX:
			workbook = new XSSFWorkbook(s);
			Log.fine("WORKBOOK", "Using XSSF Workbook structure.");
			return Format.XLSX;
		default:
			return null;
		}
	}

	public void destroyRepresentation() {
		record.flush();
		record = null;
		hasRepresentation = false;
	}

	public void createRepresentation(int quarter) {
		config.load();
		record = new ClassRecord(name, contextFileName, config, quarter);
		hasRepresentation = true;
	}

	public void patch() {
		TaskHandler.patch(record);
	}

	public void flush() {
		if (loaded) {
			Log.info("WORKBOOK", "Flushing loaded excel file...");
			try {
				workbook.close();
				if (hasRepresentation) {
					config.flush();
					record.flush();
				}
			} catch (IOException e) {
				Log.severe("WORKBOOK", "Error flushing loaded excel file.");
				e.printStackTrace();
			}
			loaded = false;
		}
	}

	public boolean hasRepresentation() {
		return hasRepresentation;
	}

	/**
	 * Scans rows in one column with a given row limit.
	 * 
	 * @param sheet
	 * @param address
	 *            - starting cell address to scan
	 * @param rowEnd
	 *            - stopping row
	 * @return scanned values
	 */
	public ArrayList<Object> verticalScan(int sheetIndex, String address, int rowEnd, boolean allowEmpty) {
		Sheet sheet = getSheet(sheetIndex);
		ArrayList<Object> scanned = new ArrayList<>();
		String[] adr = splitAddress(address);

		int columnIndex = CellReference.convertColStringToIndex(adr[0]);
		int startRow = Integer.parseInt(adr[1]);
		int currentRow = startRow;
		boolean matched = false;

		
		if (rowEnd < startRow)
			throw new ArrayIndexOutOfBoundsException("End index does not compute for addr(" + address + "); rowStart=" + startRow + ", rowEnd=" + rowEnd);

		for (Row row : sheet) {
			Cell cell = CellUtil.getCell(row, columnIndex);
			CellType type = cell.getCellTypeEnum();

			if (!matched && !isAddressMatched(cell, address))
				continue;
			matched = true;

			if (currentRow <= rowEnd) {
				if (type == CellType.BLANK && allowEmpty)
					scanned.add(cell.getStringCellValue());
				if (type == CellType.STRING)
					scanned.add(cell.getStringCellValue());
				if (type == CellType.NUMERIC || type == CellType.FORMULA)
					scanned.add(cell.getNumericCellValue());
			}
			currentRow++;
		}
		return scanned;
	}

	/**
	 * Scans columns with a given column limit.
	 * 
	 * @param sheet
	 * @param address
	 *            - starting address to scan
	 * @param columnEnd
	 *            - stopping column
	 * @return scanned values
	 */
	public ArrayList<Object> horizontalScan(int sheetIndex, String address, String columnEnd) {
		Sheet sheet = getSheet(sheetIndex);
		ArrayList<Object> scanned = new ArrayList<>();
		String[] adr = splitAddress(address);

		int startColumn = CellReference.convertColStringToIndex(adr[0]);
		int endColumn = CellReference.convertColStringToIndex(columnEnd);
		int currentColumn = startColumn;
		boolean matched = false;

		if (endColumn < startColumn)
			throw new ArrayIndexOutOfBoundsException("End index does not compute for addr(" + address + "); colStart=" + startColumn + ", colEnd=" + endColumn);
		
		for (Row row : sheet) {
			for (Cell cell : row) {
				CellType type = cell.getCellTypeEnum();

				if (!matched && !isAddressMatched(cell, address))
					continue;
				matched = true;

				if (currentColumn <= endColumn) {
					if (type == CellType.BLANK)
						scanned.add(-1);
					if (type == CellType.STRING)
						scanned.add(cell.getStringCellValue());
					if (type == CellType.NUMERIC || type == CellType.FORMULA)
						scanned.add(cell.getNumericCellValue());
				}
				currentColumn++;
			}
		}
		return scanned;
	}

	/**
	 * Check if cell is at two-dimensional row.
	 * 
	 * @param sheet
	 * @param address
	 * @return
	 */
	public Cell getCellAt(Sheet sheet, String address) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getAddress().formatAsString().equalsIgnoreCase(address))
					return cell;
			}
		}
		return null;
	}

	public Sheet getSheet(int index) {
		return workbook.getSheetAt(index);
	}

	public CellType getCellValueTypeAt(Sheet sheet, String address) {
		return getCellAt(sheet, address).getCellTypeEnum();
	}

	public CellType getCellValueTypeAt(int sheetIndex, String address) {
		Sheet sheet = getSheet(sheetIndex);
		return getCellValueTypeAt(sheet, address);
	}

	public String getCellStringValueAt(Sheet sheet, String address) {
		return getCellAt(sheet, address).getStringCellValue();
	}

	public String getCellStringValueAt(int sheetIndex, String address) {
		Sheet sheet = getSheet(sheetIndex);
		return getCellStringValueAt(sheet, address);
	}

	public double getCellNumericValueAt(int sheetIndex, String address) {
		Sheet sheet = getSheet(sheetIndex);
		return getCellAt(sheet, address).getNumericCellValue();
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	private String[] splitAddress(String address) {
		return address.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
	}

	public boolean isAddressMatched(Cell cell, String address) {
		return cell.getAddress().formatAsString().equalsIgnoreCase(address);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public ArrayList<String> getSheetNames() {
		ArrayList<String> sheetNames = new ArrayList<>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);
			sheetNames.add(sheet.getSheetName().trim());
		}
		return sheetNames;
	}

	public int getSheetCount() {
		return workbook.getNumberOfSheets();
	}

	public ContextManager getContextManager() {
		if (!hasRepresentation)
			throw new UnsupportedOperationException("Cannot retrieve ContextManager from unrepresented document.");
		return record.getContextManager();
	}

	public String getContextFileName() {
		return contextFileName;
	}

	public String getName() {
		return name;
	}

	public Format getFormat() {
		return format;
	}

	public CoreProperties getProperties() {
		if (format != Format.XLSX)
			throw new UnsupportedOperationException("Cannot operate with " + format.getExtension());

		POIXMLProperties xmlProps = ((POIXMLDocument) workbook).getProperties();
		POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
		return coreProps;
	}

	public SummaryInformation getSummaryInfo() {
		if (format != Format.XLS)
			throw new UnsupportedOperationException("Cannot operate with " + format.getExtension());

		SummaryInformation summaryInfo = ((POIDocument) workbook).getSummaryInformation();
		return summaryInfo;
	}

	public synchronized RecordConfiguration getConfig() {
		return config;
	}

	public Recognizer getSchema() {
		return schema;
	}

	public ClassRecord getClassRecord() {
		if (!hasRepresentation)
			throw new IllegalArgumentException("No class record object available for this work document.");
		return record;
	}
}
