package org.webinos.api.mediacontent;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.idl.IDLInterface;
import org.meshpoint.anode.java.Base;
import org.webinos.api.devicestatus.DevicestatusManager;

/**
 * Description: Provides access to the module functionality.
 * 
 * @author marius
 * 
 */
public abstract class MediaSourceManager extends Base {
	private static short classId = Env.getInterfaceId(DevicestatusManager.class);

	protected MediaSourceManager() {
		super(classId);
	}

	/**
	 * @return - Gets the media source object that provides access to media items stored on the device.
	 */
	public abstract MediaSource getLocalMediaSource();
}
