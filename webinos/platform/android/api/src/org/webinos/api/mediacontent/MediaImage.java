package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Dictionary;

/**
 * 
 * @author marius
 * 
 */
public class MediaImage extends MediaItem implements Dictionary {

	/**
	 * The geographical location where the image has been made.
	 */
	public SimpleCoordinates geolocation;
	public int width;
	public int height;

	/**
	 * Information about image orientation.
	 */
	public String orientation;
}
