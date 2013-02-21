/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_SortMode {

	private static Object[] __args = new Object[2];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.SortMode ob, Object[] vals) {
		ob.attributeName = (String)vals[0];
		ob.order = (org.webinos.api.mediacontent.SortModeOrder)vals[1];
	}

	public static Object[] __export(org.webinos.api.mediacontent.SortMode ob) {
		__args[0] = ob.attributeName;
		__args[1] = ob.order;
		return __args;
	}

}
