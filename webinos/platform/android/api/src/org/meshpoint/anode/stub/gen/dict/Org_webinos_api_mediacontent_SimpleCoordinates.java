/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_SimpleCoordinates {

	private static Object[] __args = new Object[3];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.SimpleCoordinates ob, Object[] vals) {
		ob.altitude = (Double)vals[0];
		ob.latitude = (Double)vals[1];
		ob.longitude = (Double)vals[2];
	}

	public static Object[] __export(org.webinos.api.mediacontent.SimpleCoordinates ob) {
		__args[0] = ob.altitude;
		__args[1] = ob.latitude;
		__args[2] = ob.longitude;
		return __args;
	}

}
