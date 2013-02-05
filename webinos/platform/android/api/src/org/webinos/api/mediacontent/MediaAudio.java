package org.webinos.api.mediacontent;

/**
 * 
 * @author marius
 *
 */
public class MediaAudio extends MediaItem {

	public String album;
	public String[] genres;
	public String[] artists;
	public String[] composers;
	public MediaLyrics lyrics;
	public String copyright;
	public long bitrate;
	public long trackNumber;
	public long duration;

	/**
	 * The timestamp that determines where the playback has been paused or
	 * stopped previously. This allows a media player application to resume
	 * playback from that point.
	 */
	public long playedTime;
	public long playCount;
}
