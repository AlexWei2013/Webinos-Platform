package org.webinos.api.mediacontent;

/**
 * Description: The interface that provides lyrics for music.
 * 
 * @author marius
 * 
 */
public class MediaLyrics {

	public MediaLyricsType type = MediaLyricsType.UNSYNCHRONIZED;
	
	/**
	 * The array of timestamps in milliseconds for lyrics. If the lyrics are not
	 * synchronized (if there is no time information for the lyrics) the array
	 * is undefined.
	 */
	public long[] timestamps;
	/**
	 * The array of lyric snippets. If the lyrics are not synchronized, the
	 * array has only one member with full lyrics.
	 */
	public String[] text;
}
