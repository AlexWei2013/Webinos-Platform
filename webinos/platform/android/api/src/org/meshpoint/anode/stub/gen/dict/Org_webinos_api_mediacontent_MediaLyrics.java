/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaLyrics {

	private static Object[] __args = new Object[3];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaLyrics ob, Object[] vals) {
		ob.text = (String[])vals[0];
		ob.timestamps = (long[])vals[1];
		ob.type = (org.webinos.api.mediacontent.MediaLyricsType)vals[2];
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaLyrics ob) {
		__args[0] = ob.text;
		__args[1] = ob.timestamps;
		__args[2] = ob.type;
		return __args;
	}

}
