/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaItem {

	private static Object[] __args = new Object[12];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaItem ob, Object[] vals) {
		ob.description = (String)vals[0];
		ob.editableAttributes = (String[])vals[1];
		ob.id = (String)vals[2];
		ob.itemURI = (String)vals[3];
		ob.mimeType = (String)vals[4];
		ob.modifiedDate = (java.util.Date)vals[5];
		ob.rating = ((org.meshpoint.anode.js.JSValue)vals[6]).dblValue;
		ob.releaseDate = (java.util.Date)vals[7];
		ob.size = ((org.meshpoint.anode.js.JSValue)vals[8]).longValue;
		ob.thumbnailURIs = (String[])vals[9];
		ob.title = (String)vals[10];
		ob.type = (org.webinos.api.mediacontent.MediaItemType)vals[11];
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaItem ob) {
		__args[0] = ob.description;
		__args[1] = ob.editableAttributes;
		__args[2] = ob.id;
		__args[3] = ob.itemURI;
		__args[4] = ob.mimeType;
		__args[5] = ob.modifiedDate;
		__args[6] = org.meshpoint.anode.js.JSValue.asJSNumber(ob.rating);
		__args[7] = ob.releaseDate;
		__args[8] = org.meshpoint.anode.js.JSValue.asJSNumber(ob.size);
		__args[9] = ob.thumbnailURIs;
		__args[10] = ob.title;
		__args[11] = ob.type;
		return __args;
	}

}
