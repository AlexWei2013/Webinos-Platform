package org.webinos.api.mediacontent;

public enum MediaFolderStorageType {
	INTERNAL(0), EXTERNAL(1), UNKNOWN(2);

	private Integer intValue;

	private MediaFolderStorageType(Integer intValue) {
		this.intValue = intValue;
	}

	@Override
	public String toString() {
		switch (this) {

		case INTERNAL:
			return "INTERNAL";
		case EXTERNAL:
			return "EXTERNAL";
		default:
			return "UNKNOWN";

		}
	}

	public int toInt() {
		switch (this) {

		case INTERNAL:
			return 0;
		case EXTERNAL:
			return 1;
		default:
			return 2;

		}
	}
}
