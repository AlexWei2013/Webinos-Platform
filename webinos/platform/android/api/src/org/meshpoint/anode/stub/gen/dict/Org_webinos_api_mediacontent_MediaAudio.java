/* This file has been automatically generated; do not edit */

package org.meshpoint.anode.stub.gen.dict;

public class Org_webinos_api_mediacontent_MediaAudio {

	private static Object[] __args = new Object[11];

	public static Object[] __getArgs() { return __args; }

	public static void __import(org.webinos.api.mediacontent.MediaAudio ob, Object[] vals) {
		ob.album = (String)vals[0];
		ob.artists = (String[])vals[1];
		ob.bitrate = (Long)vals[2];
		ob.composers = (String[])vals[3];
		ob.copyright = (String)vals[4];
		ob.duration = (Long)vals[5];
		ob.genres = (String[])vals[6];
		ob.lyrics = (org.webinos.api.mediacontent.MediaLyrics)vals[7];
		ob.playCount = (Long)vals[8];
		ob.playedTime = (Long)vals[9];
		ob.trackNumber = (Integer)vals[10];
	}

	public static Object[] __export(org.webinos.api.mediacontent.MediaAudio ob) {
		__args[0] = ob.album;
		__args[1] = ob.artists;
		__args[2] = ob.bitrate;
		__args[3] = ob.composers;
		__args[4] = ob.copyright;
		__args[5] = ob.duration;
		__args[6] = ob.genres;
		__args[7] = ob.lyrics;
		__args[8] = ob.playCount;
		__args[9] = ob.playedTime;
		__args[10] = ob.trackNumber;
		return __args;
	}

}
