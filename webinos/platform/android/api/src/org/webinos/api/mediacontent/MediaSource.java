package org.webinos.api.mediacontent;

import org.meshpoint.anode.bridge.Env;
import org.meshpoint.anode.java.Base;
import org.webinos.api.ErrorCallback;
import org.webinos.api.SuccessCallback;

/**
 * Description:interface that provides operations to retrieve and manipulate media items.
 * 
 * 
 * @author marius
 * 
 */
public abstract class MediaSource extends Base{
	
	private static short classId = Env.getInterfaceId(MediaSource.class);
	protected MediaSource() { 
		super(classId); 
		}
	
	/**
	 * Changes attributes of a media item. When an application has changed some attributes in a MediaItem,
	 * this method allows writing it back to the MediaSource.
	 * 
	 * @param item
	 *            - The media item to update.
	 */
	public abstract void updateItem(MediaItem item);

	/**
	 * Changes attributes of media items. When an application has changed any attributes in MediaItems, this
	 * method allows writing them back to the MediaSource. The errorCallback is launched with these error
	 * types: InvalidValuesError: If any of the input parameters contain an invalid value. UnknownError: In
	 * any other error case.
	 * 
	 * @param items
	 *            - Media items to change.
	 * @param successCallback
	 *            - Function called when attributes have been changed.
	 *@param errorCallback
	 *			  - Function called when an error occurred.            
	 * @return
	 */
	public abstract PendingUpdateOperation updateItemsBatch(MediaItem[] items, SuccessCallback successCallback,
			ErrorCallback errorCallback);
	
	public abstract PendingUpdateOperation updateItemsBatch(MediaItem[] items, SuccessCallback successCallback);


	/**
	 * Gets a list of media folders. This method returns (via callback) a list of media folder objects. To
	 * obtain a list of media items in a specific folder, use findItems() method with the folder ID. The
	 * errorCallback is launched with these error types: UnknownError: In any other error case.
	 * 
	 * @param successCallback
	 * @param errorCallback
	 * @return
	 */
	public abstract PendingGetOperation getFolders(MediaFolderArraySuccessCallback successCallback,
			ErrorCallback errorCallback);

	/**
	 * Finds media items. That is, satisfy conditions set in a filter. This method allows searching based on a
	 * supplied filter. For more detail on AbstractFilter, refer to the Tizen module. The filter allows
	 * precise searching such as "return all songs by artist U2, ordered by name". The errorCallback is
	 * launched with these error types: InvalidValuesError: If any of the input parameters contain an invalid
	 * value. UnknownError: In any other error case.
	 * 
	 * @param successCallback - Description: Function called when media items have been retrieved. It's possible to call
	 *            findItems again from that function to retrieve more items.
	 * @param errorCallback - Description: Function called when an error has occurred.
	 * @param folderId
	 * @param filter - Description: Filter that is used to select media items to retrieve.
	 * @param sortMode - Description: Used to determine the sort order in which the media items are returned.
	 * @param count - Description: Same as SQL LIMIT: maximum amount of items to return.
	 * @param offset - Description: with error type NotSupportedError, if this feature is not supported. with error
	 *         type SecurityError, if this functionality is not allowed. with error type TypeMismatchError, if
	 *         the input parameter is not compatible with the expected type for that parameter. Same as SQL
	 *         OFFSET: an offset of the result set.
	 * @return 
	 */
	public abstract PendingFindOperation findItems(MediaItemArraySuccessCallback successCallback,
			ErrorCallback errorCallback, String folderId, AbstractFilter filter, SortMode sortMode,
			long count, long offset);
	
	/**
	 * Used for testing, to be removed later.
	 * 
	 * @return - Some random string.
	 */
	public abstract String TestMethod();
}
