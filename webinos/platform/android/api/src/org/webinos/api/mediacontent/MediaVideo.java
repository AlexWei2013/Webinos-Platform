package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Dictionary;

/**
 * 
 * @author marius
 * 
 */
public class MediaVideo extends MediaItem implements Dictionary {

	// inherited, but ...
	public String[] editableAttributes = new String[]{"title", "description", "geolocation", "playedTime", "album", "artists"};
	
	
	/**
	 * The geographical location where the video was made.
	 * 
	 * Has three values (latitude, longitude, altitude) of type Double(nullable).
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
	public Long playedTime;
	/**
	 * The number of times the video has been played.
	 */
	public Long playCount;

}
