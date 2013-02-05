package org.webinos.api.mediacontent;

/**
 * 
 * @author marius
 * 
 */
public class MediaVideo extends MediaItem {

	/**
	 * The geographical location where the video was made.
	 */
	public SimpleCoordinates geolocation;
	public String album;
	public String[] artists;
	/**
	 * The video duration in milliseconds.
	 */
	public long duration;
	public long width;
	public long height;
	/**
	 * The timestamp that determines where the playback has been paused or
	 * stopped previously. This allows the media player application to resume
	 * playback from that point.
	 */
	public long playedTime;
	/**
	 * The number of times the video has been played.
	 */
	public long playCount;

}
