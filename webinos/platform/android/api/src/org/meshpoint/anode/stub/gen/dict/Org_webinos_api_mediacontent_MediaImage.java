/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaImage {

	private static Object[] __args = new Object[4];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaImage ob, Object[] vals) {
		ob.geolocation = (org.webinos.api.mediacontent.SimpleCoordinates)vals[0];
		ob.height = ((org.meshpoint.anode.js.JSValue)vals[1]).longValue;
		ob.orientation = (String)vals[2];
		ob.width = ((org.meshpoint.anode.js.JSValue)vals[3]).longValue;
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaImage ob) {
		__args[0] = ob.geolocation;
		__args[1] = org.meshpoint.anode.js.JSValue.asJSNumber(ob.height);
		__args[2] = ob.orientation;
		__args[3] = org.meshpoint.anode.js.JSValue.asJSNumber(ob.width);
		return __args;
	}

}
