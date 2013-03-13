/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaFolder {

	private static Object[] __args = new Object[5];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaFolder ob, Object[] vals) {
		ob.folderURI = (String)vals[0];
		ob.id = (String)vals[1];
		ob.modifiedDate = (java.util.Date)vals[2];
		ob.storageType = (String)vals[3];
		ob.title = (String)vals[4];
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaFolder ob) {
		__args[0] = ob.folderURI;
		__args[1] = ob.id;
		__args[2] = ob.modifiedDate;
		__args[3] = ob.storageType;
		__args[4] = ob.title;
		return __args;
	}

}
