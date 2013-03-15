/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_CompositeFilter {

	private static Object[] __args = new Object[2];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.CompositeFilter ob, Object[] vals) {
		ob.CompositeFilterType = (String)vals[0];
		ob.filters = (org.webinos.api.mediacontent.AbstractFilter[])vals[1];
	}

	public static Object[] __export(org.webinos.api.mediacontent.CompositeFilter ob) {
		__args[0] = ob.CompositeFilterType;
		__args[1] = ob.filters;
		return __args;
	}

}
