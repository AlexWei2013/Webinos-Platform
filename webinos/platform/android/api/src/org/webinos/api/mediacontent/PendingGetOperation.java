package org.webinos.api.mediacontent;

/**
 * Description: describes the object that is returned by the asynchronous
 * getFolders method. It makes it possible to stop this operation if it hasn't
 * produced a result within a desired time.
 * 
 * @author marius
 * 
 */
public interface PendingGetOperation {
	/**
	 * Cancel the pending getFolders asynchronous operation. When this method is
	 * called, the user agent must stop and allocated resources should be
	 * released. An error callback is issued with the DOMError name
	 * "AbortError".
	 */
	public void cancel();
}
