package local.vitre.desktop.record;

public enum ComponentType {
	WRITTEN_WORK("writtenWork"), PERFORMANCE_TASK("performanceTask"), QUARTERLY_ASSESSMENT(
			"quarterlyAssessment"), ALL("all");

	private String tag;

	private ComponentType(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public static ComponentType getType(String tag) {
		for (ComponentType type : values())
			if (type.getTag().equals(tag))
				return type;
		throw new NullPointerException("No component type found with that tag.");
	}
}