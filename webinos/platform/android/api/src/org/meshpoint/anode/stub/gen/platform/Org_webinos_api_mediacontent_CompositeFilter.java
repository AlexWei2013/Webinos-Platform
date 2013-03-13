/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_CompositeFilter {

	private static Object[] __args = new Object[0];

	public static Object[] __getArgs() { return __args; }

	static Object __get(org.webinos.api.mediacontent.CompositeFilter inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* CompositeFilterType */
			result = inst.CompositeFilterType;
			break;
		case 1: /* filters */
			result = inst.filters;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.CompositeFilter inst, int attrIdx, Object val) {
		switch(attrIdx) {
		case 0: /* CompositeFilterType */
			inst.CompositeFilterType = (String)val;
			break;
		case 1: /* filters */
			inst.filters = (org.webinos.api.mediacontent.AbstractFilter[])val;
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
