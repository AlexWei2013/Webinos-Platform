/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_AttributeFilter {

	private static Object[] __args = new Object[0];

	public static Object[] __getArgs() { return __args; }

	static Object __get(org.webinos.api.mediacontent.AttributeFilter inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* attributeName */
			result = inst.attributeName;
			break;
		case 1: /* filterMatchFlag */
			result = inst.filterMatchFlag;
			break;
		case 2: /* matchValue */
			result = inst.matchValue;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.AttributeFilter inst, int attrIdx, Object val) {
		switch(attrIdx) {
		case 0: /* attributeName */
			inst.attributeName = (String)val;
			break;
		case 1: /* filterMatchFlag */
			inst.filterMatchFlag = (String)val;
			break;
		case 2: /* matchValue */
			inst.matchValue = val;
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
