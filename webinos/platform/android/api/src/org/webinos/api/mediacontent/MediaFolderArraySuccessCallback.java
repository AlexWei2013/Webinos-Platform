package org.webinos.api.mediacontent;

import org.meshpoint.anode.idl.Callback;

/**
 * 
 * Description: callback function used to return a list of MediaFolder objects
 * 
 * @author marius
 * 
 */
public interface MediaFolderArraySuccessCallback extends Callback {
	public void onsuccess(MediaFolder[] folders);
}
