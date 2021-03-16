package local.vitre.desktop.ui;

import org.controlsfx.control.PopOver;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import local.vitre.desktop.record.ClassRecordDescriptor;

public class SignaturePopOver extends PopOver {

	public VBox box;
	public TextFlow classSigFlow;
	public TextFlow subjectSigFlow;
	public TextFlow teacherSigFlow;

	public Text valueClassSig;
	public Text valueSubjectSig;
	public Text valueTeacherSig;

	public TextFlow classIDFlow;
	public TextFlow subjectIDFlow;
	public TextFlow teacherIDFlow;

	public Text valueClassID;
	public Text valueSubjectID;
	public Text valueTeacherID;

	public ClassRecordDescriptor descriptor;

	public SignaturePopOver() {

		// Signature fields
		classSigFlow = new TextFlow();
		subjectSigFlow = new TextFlow();
		teacherSigFlow = new TextFlow();

		Text labelSigHeader = new Text("Signatures");
		labelSigHeader.setTextAlignment(TextAlignment.CENTER);

		Text labelClass = new Text("Class: ");
		Text labelSubject = new Text("Subject: ");
		Text labelTeacher = new Text("Teacher: ");

		// Signature value fields
		valueClassSig = new Text();
		valueSubjectSig = new Text();
		valueTeacherSig = new Text();

		classSigFlow.setMinWidth(Region.USE_PREF_SIZE);
		subjectSigFlow.setMinWidth(Region.USE_PREF_SIZE);
		teacherSigFlow.setMinWidth(Region.USE_PREF_SIZE);

		sigValueFill(Color.DARKGRAY);

		classSigFlow.getChildren().addAll(labelClass, valueClassSig);
		subjectSigFlow.getChildren().addAll(labelSubject, valueSubjectSig);
		teacherSigFlow.getChildren().addAll(labelTeacher, valueTeacherSig);

		// ID fields
		classIDFlow = new TextFlow();
		subjectIDFlow = new TextFlow();
		teacherIDFlow = new TextFlow();

		Text labelIDHeader = new Text("Network IDs");
		labelIDHeader.setTextAlignment(TextAlignment.CENTER);

		Text labelClassID = new Text("Class: ");
		Text labelSubjectID = new Text("Subject: ");
		Text labelTeacherID = new Text("Teacher: ");

		valueClassID = new Text();
		valueSubjectID = new Text();
		valueTeacherID = new Text();

		idValueFill(Color.DARKGRAY);

		classIDFlow.getChildren().addAll(labelClassID, valueClassID);
		subjectIDFlow.getChildren().addAll(labelSubjectID, valueSubjectID);
		teacherIDFlow.getChildren().addAll(labelTeacherID, valueTeacherID);

		box = new VBox(labelIDHeader, classIDFlow, subjectIDFlow, teacherIDFlow, labelSigHeader, classSigFlow,
				subjectSigFlow, teacherSigFlow);
		box.setAlignment(Pos.TOP_CENTER);
		box.setPadding(new Insets(5));
		box.setPrefWidth(200);
		box.setPrefHeight(50);

		this.setStyle("-fx-font-size: 9pt;");
		setTitle("Authentication Information");
		setContentNode(box);
		setDetachable(true);
		setCloseButtonEnabled(false);

		setArrowLocation(ArrowLocation.TOP_CENTER);
	}

	public void attachDescriptor(ClassRecordDescriptor descriptor) {
		this.descriptor = descriptor;
		valueClassSig.setText(descriptor.classSignature);
		valueSubjectSig.setText(descriptor.subjectSignature);
		valueTeacherSig.setText(descriptor.teacherSignature);

		valueClassID.setText(String.valueOf(descriptor.classID));
		valueSubjectID.setText(String.valueOf(descriptor.subjectID));
		valueTeacherID.setText(String.valueOf(descriptor.teacherID));
	}

	public void idValueFill(Color c) {
		valueClassID.setFill(c);
		valueSubjectID.setFill(c);
		valueTeacherID.setFill(c);
	}

	public void idValueFill(boolean hasClass, boolean hasSubject, boolean hasTeacher) {
		// Unsynced
		Color r = Color.DARKRED;
		// Synced
		Color g = Color.DARKGREEN;

		if (hasClass)
			valueClassID.setFill(g);
		else
			valueClassID.setFill(r);

		if (hasSubject)
			valueSubjectID.setFill(g);
		else
			valueSubjectID.setFill(r);

		if (hasTeacher)
			valueTeacherID.setFill(g);
		else
			valueTeacherID.setFill(r);
	}

	public void sigValueFill(Color c) {
		valueClassSig.setFill(c);
		valueSubjectSig.setFill(c);
		valueTeacherSig.setFill(c);
	}

	public void sigValueFill(boolean hasClass, boolean hasSubject, boolean hasTeacher) {
		// Unsynced
		Color r = Color.DARKRED;
		// Synced
		Color g = Color.DARKGREEN;

		if (hasClass)
			valueClassSig.setFill(g);
		else
			valueClassSig.setFill(r);

		if (hasSubject)
			valueSubjectSig.setFill(g);
		else
			valueSubjectSig.setFill(r);

		if (hasTeacher)
			valueTeacherSig.setFill(g);
		else
			valueTeacherSig.setFill(r);
	}

}
