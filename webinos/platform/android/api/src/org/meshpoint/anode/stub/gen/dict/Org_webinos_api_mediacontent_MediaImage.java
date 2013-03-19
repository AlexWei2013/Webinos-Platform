/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaImage {

	private static Object[] __args = new Object[5];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaImage ob, Object[] vals) {
		ob.editableAttributes = (String[])vals[0];
		ob.geolocation = (org.webinos.api.mediacontent.SimpleCoordinates)vals[1];
		ob.height = (int)((org.meshpoint.anode.js.JSValue)vals[2]).longValue;
		ob.orientation = (String)vals[3];
		ob.width = (int)((org.meshpoint.anode.js.JSValue)vals[4]).longValue;
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaImage ob) {
		__args[0] = ob.editableAttributes;
		__args[1] = ob.geolocation;
		__args[2] = org.meshpoint.anode.js.JSValue.asJSNumber((long)ob.height);
		__args[3] = ob.orientation;
		__args[4] = org.meshpoint.anode.js.JSValue.asJSNumber((long)ob.width);
		return __args;
	}

}
