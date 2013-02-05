package org.webinos.api.mediacontent;

import java.util.Date;

public class MediaFolder {
	
	public String id;
	public String folderURI;
	public String title;
	public MediaFolderStorageType storageType;
	public Date modifiedDate;
	
	
	/*
	 * Is it necessary to extend Base from org.meshpoint.anode.java ? For what?
	 */
//	private static short classId = Env.getInterfaceId(Contact.class);
//	protected MediaFolder() { super(classId); }
}
