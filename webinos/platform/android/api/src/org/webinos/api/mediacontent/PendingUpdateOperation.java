package org.webinos.api.mediacontent;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;

/**
 * Description: the object that is returned by the asynchronous updateItemsBatch
 * method. It makes it possible to stop this operation if it hasn't produced a
 * result within a desired time. Most likely a part of items will be already
 * updated.
 * 
 * @author marius
 * 
 */
public abstract class PendingUpdateOperation extends Base {

	private static short classId = Env.getInterfaceId(PendingUpdateOperation.class);
	protected PendingUpdateOperation() { super(classId); }
	
	/**
	 * Cancel the pending updateItemsBatch asynchronous operation. When this
	 * method is called, the user agent must stop; all items must be or
	 * completely updated or left untouched (no item should be partially
	 * updated). Allocated resources should be released. An error callback is
	 * issued with the DOMError name "AbortError".
	 */
	public abstract void cancel();
}