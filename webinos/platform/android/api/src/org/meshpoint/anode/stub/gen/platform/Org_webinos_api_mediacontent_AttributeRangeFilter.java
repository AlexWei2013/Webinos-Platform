/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_AttributeRangeFilter {

	private static Object[] __args = new Object[0];

	public static Object[] __getArgs() { return __args; }

	static Object __get(org.webinos.api.mediacontent.AttributeRangeFilter inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* attributeName */
			result = inst.attributeName;
			break;
		case 1: /* endValue */
			result = inst.endValue;
			break;
		case 2: /* initialValue */
			result = inst.initialValue;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.AttributeRangeFilter inst, int attrIdx, Object val) {
		switch(attrIdx) {
		case 0: /* attributeName */
			inst.attributeName = (String)val;
			break;
		case 1: /* endValue */
			inst.endValue = val;
			break;
		case 2: /* initialValue */
			inst.initialValue = val;
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
