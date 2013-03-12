package org.webinos.api.mediacontent;

public enum MediaItemType {
	IMAGE(0), VIDEO(1), AUDIO(2), UNKNOWN(3);

	private int index;

	private MediaItemType(int idx) {
		this.index = idx;
	}

	@Override
	public String toString() {

		switch (this) {
		case IMAGE:
			return "IMAGE";
		case VIDEO:
			return "VIDEO";
		case AUDIO:
			return "AUDIO";
		default:
			return "UNKNOWN";
		}
	}

	public Integer toInteger() {
		switch (this) {
		case IMAGE:
			return 0;
		case VIDEO:
			return 1;
		case AUDIO:
			return 2;
		default:
			return 3;
		}
	}

}
