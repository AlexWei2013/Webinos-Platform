package org.webinos.api.mediacontent;

import java.util.Date;

import org.meshpoint.anode.idl.Dictionary;

public class MediaItem implements Dictionary{
	// An array of attributes that can be changed and persisted to storage
	public String[] editableAttributes = new String[]{"title", "description", "rating"};
	public String id;
	/**
	 * Use the MediaItemType enum to choose a type and then call its toString() method
	 */
	public String type;
	public String mimeType;
	public String title;
	public String itemURI;
	public String[] thumbnailURIs;
	public Date releaseDate;
	public Date modifiedDate;
	public Long size;
	public String description;
	public Double rating;

}


