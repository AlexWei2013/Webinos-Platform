package org.webinos.api.mediacontent;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

/**
 * Description: describes the object that is returned by the asynchronous
 * getFolders method. It makes it possible to stop this operation if it hasn't
 * produced a result within a desired time.
 * 
 * @author marius
 * 
 */
public abstract class PendingGetOperation extends Base {

	private static short classId = Env.getInterfaceId(PendingGetOperation.class);
	protected PendingGetOperation() { super(classId); }
	
	/**
	 * Cancel the pending getFolders asynchronous operation. When this method is
	 * called, the user agent must stop and allocated resources should be
	 * released. An error callback is issued with the DOMError name
	 * "AbortError".
	 */
	public abstract void cancel();
}