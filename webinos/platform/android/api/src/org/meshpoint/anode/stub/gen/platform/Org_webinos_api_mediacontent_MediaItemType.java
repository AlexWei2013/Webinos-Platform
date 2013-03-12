/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_MediaItemType {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(org.webinos.api.mediacontent.MediaItemType inst, int opIdx, Object[] args) {
		Object result = null;
		switch(opIdx) {
		case 0: /* toInteger */
			result = inst.toInteger();
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

	static Object __get(org.webinos.api.mediacontent.MediaItemType inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* AUDIO */
			result = org.webinos.api.mediacontent.MediaItemType.AUDIO;
			break;
		case 1: /* IMAGE */
			result = org.webinos.api.mediacontent.MediaItemType.IMAGE;
			break;
		case 2: /* UNKNOWN */
			result = org.webinos.api.mediacontent.MediaItemType.UNKNOWN;
			break;
		case 3: /* VIDEO */
			result = org.webinos.api.mediacontent.MediaItemType.VIDEO;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.MediaItemType inst, int attrIdx, Object val) {
		switch(attrIdx) {
		default:
			throw new UnsupportedOperationException();
		}
	}

}
