/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_FilterMatchFlag {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(org.webinos.api.mediacontent.FilterMatchFlag inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* toInteger */
			result = org.meshpoint.anode.js.JSValue.asJSNumber((long)inst.toInteger());
			break;
		case 1: /* toString */
			result = inst.toString();
			break;
		case 2: /* valueOf */
			result = inst.valueOf(
				(String)args[0]
			);
			break;
		case 3: /* values */
			result = inst.values();
			break;
		default:
		}
		return result;
	}

	static Object __get(org.webinos.api.mediacontent.FilterMatchFlag inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* CONTAINS */
			result = org.webinos.api.mediacontent.FilterMatchFlag.CONTAINS;
			break;
		case 1: /* ENDSWITH */
			result = org.webinos.api.mediacontent.FilterMatchFlag.ENDSWITH;
			break;
		case 2: /* EXACTLY */
			result = org.webinos.api.mediacontent.FilterMatchFlag.EXACTLY;
			break;
		case 3: /* EXISTS */
			result = org.webinos.api.mediacontent.FilterMatchFlag.EXISTS;
			break;
		case 4: /* FULLSTRING */
			result = org.webinos.api.mediacontent.FilterMatchFlag.FULLSTRING;
			break;
		case 5: /* STARTSWITH */
			result = org.webinos.api.mediacontent.FilterMatchFlag.STARTSWITH;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.FilterMatchFlag inst, int attrIdx, Object val) {
		switch(attrIdx) {
		default:
			throw new UnsupportedOperationException();
		}
	}

}
