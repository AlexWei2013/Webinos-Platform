package org.webinos.api.mediacontent;

import java.util.Date;

import org.meshpoint.anode.idl.Dictionary;

public class MediaItem implements Dictionary{
	public String[] editableAttributes;
	public String id;
	public MediaItemType type;
	public String mimeType;
	public String title;
	public String itemURI;
	public String[] thumbnailURIs;
	public Date releaseDate;
	public Date modifiedDate;
	public long size;
	public String description;
	public  double rating;

}
