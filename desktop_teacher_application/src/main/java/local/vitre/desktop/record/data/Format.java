package local.vitre.desktop.record.data;

public enum Format {

	XLS("xls"), XLSX("xlsx");

	private String ext;

	Format(String ext) {
		this.ext = ext;
	}

	public String getExtension() {
		return ext;
	}

	public static Format getFormat(String ext) {
		for (Format f : values())
			if (f.getExtension().equalsIgnoreCase(ext))
				return f;
		throw new NullPointerException("No file format found with that type.");
	}
}