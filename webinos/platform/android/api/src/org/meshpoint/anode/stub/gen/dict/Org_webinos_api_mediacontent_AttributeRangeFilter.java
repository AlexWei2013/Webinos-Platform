/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_AttributeRangeFilter {

	private static Object[] __args = new Object[3];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.AttributeRangeFilter ob, Object[] vals) {
		ob.attributeName = (String)vals[0];
		ob.endValue = vals[1];
		ob.initialValue = vals[2];
	}

	public static Object[] __export(org.webinos.api.mediacontent.AttributeRangeFilter ob) {
		__args[0] = ob.attributeName;
		__args[1] = ob.endValue;
		__args[2] = ob.initialValue;
		return __args;
	}

}
