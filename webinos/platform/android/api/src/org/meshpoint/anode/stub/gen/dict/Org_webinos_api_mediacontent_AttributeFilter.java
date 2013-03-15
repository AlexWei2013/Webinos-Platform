/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_AttributeFilter {

	private static Object[] __args = new Object[3];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.AttributeFilter ob, Object[] vals) {
		ob.attributeName = (String)vals[0];
		ob.filterMatchFlag = (String)vals[1];
		ob.matchValue = vals[2];
	}

	public static Object[] __export(org.webinos.api.mediacontent.AttributeFilter ob) {
		__args[0] = ob.attributeName;
		__args[1] = ob.filterMatchFlag;
		__args[2] = ob.matchValue;
		return __args;
	}

}
