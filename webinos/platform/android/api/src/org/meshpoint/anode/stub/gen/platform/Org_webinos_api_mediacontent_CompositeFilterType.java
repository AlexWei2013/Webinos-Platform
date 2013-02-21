/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_CompositeFilterType {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(org.webinos.api.mediacontent.CompositeFilterType inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* valueOf */
			result = inst.valueOf(
				(String)args[0]
			);
			break;
		case 1: /* values */
			result = inst.values();
			break;
		default:
		}
		return result;
	}

	static Object __get(org.webinos.api.mediacontent.CompositeFilterType inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* INTERSECTION */
			result = org.webinos.api.mediacontent.CompositeFilterType.INTERSECTION;
			break;
		case 1: /* UNION */
			result = org.webinos.api.mediacontent.CompositeFilterType.UNION;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.CompositeFilterType inst, int attrIdx, Object val) {
		switch(attrIdx) {
		default:
			throw new UnsupportedOperationException();
		}
	}

}
