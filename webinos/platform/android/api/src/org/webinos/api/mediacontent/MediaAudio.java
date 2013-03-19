package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Dictionary;

/**
 * 
 * @author marius
 *
 */
public class MediaAudio extends MediaItem implements Dictionary{


	public String[] editableAttributes = new String[] { "title", "album", "artists", "composers",
														"trackNumber", "playedTime" };
	
	
	public String album;
	public String[] genres;
	public String[] artists;
	public String[] composers;
	public MediaLyrics lyrics;
	public String copyright;
	public Long bitrate;
	public Integer trackNumber;
	public Long duration;

	/**
	 * The timestamp that determines where the playback has been paused or
	 * stopped previously. This allows a media player application to resume
	 * playback from that point.
	 */
	public Long playedTime;
	public Long playCount;
}
