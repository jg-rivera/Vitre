package local.vitre.desktop.record.patch;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import javafx.application.Platform;
import local.vitre.desktop.Log;
import local.vitre.desktop.Vitre;
import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.http.NetworkManager;
import local.vitre.desktop.record.ClassRecord;
import local.vitre.desktop.record.ClassRecordDescriptor;
import local.vitre.desktop.record.patch.diff.PatchDifference;
import local.vitre.desktop.record.patch.diff.PatchInstructionSet;
import local.vitre.desktop.record.patch.file.ClassPatch;
import local.vitre.desktop.record.patch.file.GradePatch;
import local.vitre.desktop.record.patch.file.PatchFile.PatchType;
import local.vitre.desktop.record.patch.queue.PatchQueueManager;
import local.vitre.desktop.record.print.AccountSheet;
import local.vitre.desktop.ui.UIHandler;

public class Patcher {

	private String patchSignature;
	private PatchInstructionSet instructionSet;
	private PatchCrown crown;
	private PatchDifference diff;

	private GradePatch contentPatch;

	private ClassRecord record;
	private boolean sync;

	public Patcher(ClassRecord record) {
		this.record = record;
	}

	public void synchronize() throws Exception {
		// Handle resynchronization
		if (isSynchronized()) {
			crown = null;
			contentPatch = null;
			diff = null;
			instructionSet = null;
			patchSignature = null;
		}

		createCrown();
		createContentPatch();
		createDiff();

		if (hasSignature()) {
			Platform.runLater(() -> updateUI());
			Log.severe("PATCH", "Synchronization complete.");
			setSynchronized(true);
			return;
		}
		throw new IllegalStateException("Unable to synchronize.");
	}

	private void updateUI() {
		UIHandler.toggleCheck(Vitre.controller.checkLabelSync, true);
		Vitre.controller.patchBtn.setDisable(false);
		UIHandler.repaintStudentList();
	}

	/**
	 * Main patching process.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void registerGrades() throws ClientProtocolException, IOException, ParseException {
		if (!isSynchronized()) {
			Log.fine("PATCH", "Attempted to register grades but was not synchronized.");
			return;
		}

		createContentPatch();

		String inst = Cypher.pseudoEncrypt(instructionSet.getInstructions());
		String patchBody = writeContentPatch();

		Log.info("Publishing grade data patch for " + record.getName() + " to server.");
		Log.fine("--------------");
		Log.fine("Section", record.gradeSection);
		Log.fine("Subject", record.subject);
		Log.fine("Teacher", record.teacher);
		Log.fine("--------------");
		Log.fine("Sig", patchSignature);
		Log.fine("InstSet", inst);
		Log.fine("PLoad", patchBody);

		PatchQueueManager queuer = Vitre.getQueueManager();
		queuer.add(diff, new ClassRecordDescriptor(record));

		NetworkManager.uploadPatchData(patchSignature, patchBody, inst, PatchType.CONTENT);
	}

	public void registerStudents() throws ClientProtocolException, IOException, ParseException {
		createClassPatch();
	}

	public void createContentPatch() {
		if (!hasCrown()) {
			Log.warn("No available patch crown.");
			return;
		}

		GradePatch gradePatch = new GradePatch();
		gradePatch.attachCrown(crown);
		gradePatch.attachData(record);

		if (!gradePatch.isSigned()) {
			Log.warn("Cannot attach unsigned crown header.");
			return;
		}

		String signature = crown.getSignature();
		gradePatch.setFileName(signature);
		contentPatch = gradePatch;
	}

	public void createAccountSheet(String outputPath) {
		Log.info("PATCH", "Generating account sheet for " + record.getName());
		try {
			AccountSheet doc = new AccountSheet(record.getStudents(), record.gradeSection);
			doc.write(outputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.info("PATCH", "Account sheet generated.");
	}

	public String writeContentPatch() {
		if (contentPatch == null)
			return null;
		if (!contentPatch.isSigned()) {
			Log.warn("Cannot attach unsigned crown header.");
			return null;
		}

		return contentPatch.write(PatchType.CACHE);
	}

	/**
	 * Adviser's function to register a class to the Vitre.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createClassPatch() throws ClientProtocolException, IOException, ParseException {
		if (!hasCrown()) {
			Log.warn("No available patch crown.");
			return;
		}

		ClassPatch classPatch = new ClassPatch(record.gradeSection);
		classPatch.storeToStudentTree(record.getClassNetworkID(), record.getClassSignature(), record.getClassChunk(),
				record.gradeSection, record.getStudents());

		String pload = classPatch.write(PatchType.CLASS);
		String signature = record.getClassSignature();

		NetworkManager.uploadPatchData(signature, pload, null, PatchType.CLASS);

	}

	public void createCrown() {
		PatchCrown crown = new PatchCrown();
		crown.validate(record);

		if (!crown.isSigned())
			return;

		setPatchSignature(crown.getSignature());
		setCrown(crown);
	}

	public void createDiff() throws JsonProcessingException, ParseException, IOException {
		if (!hasCrown()) {
			Log.warn("No available patch crown.");
			return;
		}
		String newData = contentPatch.toString();
		String cacheData = getCacheData();
		int count = record.getStudentsCount();

		// No server cache data.
		if (cacheData == null) {
			diff = new PatchDifference(count, newData);
			diff.updateTableCells();
		} else {
			cacheData = Cypher.payload(cacheData);
			diff = new PatchDifference(count, cacheData, newData);
			diff.updateTableCells();
		}

		instructionSet = diff.createInstructionSet();
	}

	public void setCrown(PatchCrown crown) {
		this.crown = crown;
	}

	public PatchCrown getCrown() {
		return crown;
	}

	public boolean hasCrown() {
		return crown != null;
	}

	public void removeCrown() {
		this.crown = null;
	}

	public boolean hasSignature() {
		return patchSignature != null;
	}

	public void setPatchSignature(String signature) {
		this.patchSignature = signature;
	}

	public GradePatch getContentPatch() {
		return contentPatch;
	}

	public PatchInstructionSet getInstructionSet() {
		return instructionSet;
	}

	public PatchDifference getDiff() {
		return diff;
	}

	public String getCacheData() {
		if (!hasSignature())
			return null;

		Log.fine("SYNC", "Retrieving server cache data for " + patchSignature);

		try {
			return retrieveServerCacheData();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			Log.warn("Unable to retrieve server cache file for " + patchSignature);
		}

		return null;
	}

	public String retrieveServerCacheData() throws ClientProtocolException, IOException, ParseException {
		if (!hasSignature())
			return null;
		return NetworkManager.retrieveServerFileCache(appendExt(patchSignature));
	}

	public void setSynchronized(boolean sync) {
		this.sync = sync;
	}

	public boolean isSynchronized() {
		return sync;
	}

	private String appendExt(String text) {
		return text + PatchType.CACHE.extension;
	}

}
