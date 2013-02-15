package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Callback;

/**
 * Description: callback function used to return a list of MediaItem objects.
 * 
 * 
 * @author marius
 * 
 */
public interface MediaItemArraySuccessCallback extends Callback {
	public void onsuccess(MediaItem[] items);
}
