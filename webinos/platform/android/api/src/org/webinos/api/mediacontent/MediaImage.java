package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Dictionary;

/**
 * 
 * @author marius
 * 
 */
public class MediaImage extends MediaItem implements Dictionary {
	
	public String[] editableAttributes = new String[]{"title", "description", "geolocation", "orientation"};

	/**
	 * The geographical location where the image has been made.
	 */
	public SimpleCoordinates geolocation;
	public int width;
	public int height;

	/**
	 * Information about image orientation.
	 * 
	 * Values: NORMAL, FLIP_HORIZONTAL, ROTATE_180, FLIP_VERTICAL, TRANSPOSE, ROTATE_90, TRANSVERSE, ROTATE_270.
	 */
	public String orientation;
}
