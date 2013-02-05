package org.webinos.api.mediacontent;

/**
 * 
 * @author marius
 * 
 */
public class MediaImage extends MediaItem {

	/**
	 * The geographical location where the image has been made.
	 */
	public SimpleCoordinates geolocation;
	public long width;
	public long height;

	/**
	 * Information about image orientation.
	 */
	public MediaImageOrientation orientation;
}
