package local.vitre.desktop.record;

import local.vitre.desktop.Log;
import local.vitre.desktop.http.Cypher;
import local.vitre.desktop.util.Utils;

public class Student {

	private String name;
	private String username;
	private String password;
	private String signature;

	public enum Gender {
		MALE('M'), FEMALE('F');
		private char rep;

		private Gender(char rep) {
			this.rep = rep;
		}

		public char getRepresentation() {
			return rep;
		}
	}

	private Gender gender;
	private ClassRecord record;
	private int index;
	private int offset;

	public Student(ClassRecord record, Gender gender, String name, int index) {
		this.record = record;
		this.name = name;
		this.index = index;
		this.gender = gender;

		String chunk = Utils.chunkify(name);
		this.username = Utils.makeUsername(name);
		this.password = Utils.createHumanPassword(10);
		this.signature = Cypher.sha256(chunk);

		if (gender == Gender.FEMALE)
			offset = record.getMaleStudentsCount();

		Log.fine("RECORD", "Student: " + name + " registered into " + gender.name() + " LIST. Username=" + username
				+ ", Index=" + getIndex() + ", Chk=" + chunk + ", Sig=" + signature);
	}

	@Override
	public String toString() {
		return getIndex() + ". " + name;
	}

	public String getName() {
		return name;
	}

	public String getInitials() {
		String[] com = getFirstName().split("[^A-Z]+");
		String initials = "";

		for (int i = 0; i < com.length; i++)
			initials += com[i];
		return initials;
	}

	public String getSignature() {
		return signature;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getIndex() {
		return index + offset;
	}

	public int getOffset() {
		return offset;
	}

	public String getFirstName() {
		return Utils.getFirstName(name);
	}

	public String getLastName() {
		return Utils.getLastName(name);
	}

	public Gender getGender() {
		return gender;
	}

	public ClassRecord getRecord() {
		return record;
	}
}
