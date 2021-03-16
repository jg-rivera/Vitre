package local.vitre.desktop.record;

public enum TrackType {
	CORE("core"), ACADEMIC("academic"), IMMERSION("immersion"), TVL("TVL");

	private String keyword;

	private TrackType(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public static TrackType getMatch(String track) {
		for (TrackType type : values())
			if (track.toLowerCase().contains(type.getKeyword().toLowerCase()))
				return type;
		return null;
	}
}