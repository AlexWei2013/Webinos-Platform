package org.webinos.api.mediacontent;

/**
 * Defines whether a lyric supplied with the audio file is time-synchronized or not.
 * 
 * Values: SYNCHRONIZED, UNSYNCHRONIZED
 * 
 * @author marius
 * 
 */
public enum MediaLyricsType {
	SYNCHRONIZED(0), UNSYNCHRONIZED(1);

	private int index;

	private MediaLyricsType(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		switch (this) {
		case SYNCHRONIZED:
			return "SYNCHRONIZED";
		case UNSYNCHRONIZED:
			return "UNSYNCHRONIZED";
		default:
			throw new IllegalStateException("Not a predefined value of MediaLyricsType enum!");
		}
	}

	public int toInteger() {
		// TODO Auto-generated method stub
		switch (this) {
		case SYNCHRONIZED:
			return 0;
		case UNSYNCHRONIZED:
			return 1;
		default:
			throw new IllegalStateException("Not a predefined value of MediaLyricsType enum!");
		}
	}
}
