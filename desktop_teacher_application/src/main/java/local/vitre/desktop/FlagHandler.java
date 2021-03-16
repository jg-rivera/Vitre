package local.vitre.desktop;

import java.util.HashSet;

import local.vitre.desktop.ui.UIHandler;

public class FlagHandler {

	public enum Flag {
		UNREGISTERED_CLASS("Unregistered class."), 
		UNREGISTERED_SUBJECT("Unregistered subject."), 
		UNREGISTERED_TEACHER("Unregistered teacher."), 
		EMPTY_CLASS("Unpopulated class. No students registered."),	
		CANNOT_CONNECT("Cannot communicate with server."),
		CANNOT_SYNC("Cannot synchronize.");
		
		private String desc;

		private Flag(String desc) {
			this.desc = desc;
		}

		public String getDescription() {
			return desc;
		}
	}

	public HashSet<Flag> flags;

	public FlagHandler() {
		flags = new HashSet<>();
		Log.info("FLAG", "Flag handler initialized.");
	}

	public void add(Flag flag) {
		flags.add(flag);
		UIHandler.refreshCounter();
	}

	public void remove(Flag flag) {
		if (has(flag)) {
			flags.remove(flag);
			UIHandler.refreshCounter();
		}
	}

	public void clear() {
		flags.clear();
		UIHandler.refreshCounter();
	}

	public boolean has(Flag flag) {
		return flags.contains(flag);
	}

	public int size() {
		return flags.size();
	}
}
