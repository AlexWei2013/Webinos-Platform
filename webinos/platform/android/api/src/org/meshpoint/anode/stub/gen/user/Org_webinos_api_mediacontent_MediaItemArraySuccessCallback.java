/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.user;

public class Org_webinos_api_mediacontent_MediaItemArraySuccessCallback extends org.meshpoint.anode.js.JSInterface implements org.webinos.api.mediacontent.MediaItemArraySuccessCallback {

	private static int classId = org.meshpoint.anode.bridge.Env.getInterfaceId(org.webinos.api.mediacontent.MediaItemArraySuccessCallback.class);

	Org_webinos_api_mediacontent_MediaItemArraySuccessCallback(long instHandle) { super(instHandle); }

	public void finalize() { super.release(classId); }

	private static Object[] __args = new Object[1];

	public void onsuccess(org.webinos.api.mediacontent.MediaItem[] arg0) {
		__args[0] = arg0;
		__invoke(classId, 0, __args);
	}

}
