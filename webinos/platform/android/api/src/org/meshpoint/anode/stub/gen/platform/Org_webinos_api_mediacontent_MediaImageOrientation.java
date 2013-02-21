/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.platform;

public class Org_webinos_api_mediacontent_MediaImageOrientation {

	private static Object[] __args = new Object[1];

	public static Object[] __getArgs() { return __args; }

	static Object __invoke(org.webinos.api.mediacontent.MediaImageOrientation inst, int opIdx, Object[] args) {
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

	static Object __get(org.webinos.api.mediacontent.MediaImageOrientation inst, int attrIdx) {
		Object result = null;
		switch(attrIdx) {
		case 0: /* FLIP_HORIZONTAL */
			result = org.webinos.api.mediacontent.MediaImageOrientation.FLIP_HORIZONTAL;
			break;
		case 1: /* FLIP_VERTICAL */
			result = org.webinos.api.mediacontent.MediaImageOrientation.FLIP_VERTICAL;
			break;
		case 2: /* NORMAL */
			result = org.webinos.api.mediacontent.MediaImageOrientation.NORMAL;
			break;
		case 3: /* ROTATE_180 */
			result = org.webinos.api.mediacontent.MediaImageOrientation.ROTATE_180;
			break;
		case 4: /* ROTATE_270 */
			result = org.webinos.api.mediacontent.MediaImageOrientation.ROTATE_270;
			break;
		case 5: /* ROTATE_90 */
			result = org.webinos.api.mediacontent.MediaImageOrientation.ROTATE_90;
			break;
		case 6: /* TRANSPOSE */
			result = org.webinos.api.mediacontent.MediaImageOrientation.TRANSPOSE;
			break;
		case 7: /* TRANSVERSE */
			result = org.webinos.api.mediacontent.MediaImageOrientation.TRANSVERSE;
			break;
		default:
		}
		return result;
	}

	static void __set(org.webinos.api.mediacontent.MediaImageOrientation inst, int attrIdx, Object val) {
		switch(attrIdx) {
		default:
			throw new UnsupportedOperationException();
		}
	}

}
