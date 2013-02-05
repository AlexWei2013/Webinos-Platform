package org.webinos.impl.MediaContent;

import java.util.Date;

import org.webinos.api.ErrorCallback;
import org.webinos.api.SuccessCallback;
import org.webinos.api.mediacontent.AbstractFilter;
import org.webinos.api.mediacontent.MediaFolder;
import org.webinos.api.mediacontent.MediaFolderArraySuccessCallback;
import org.webinos.api.mediacontent.MediaFolderStorageType;
import org.webinos.api.mediacontent.MediaItem;
import org.webinos.api.mediacontent.MediaItemArraySuccessCallback;
import org.webinos.api.mediacontent.MediaSource;
import org.webinos.api.mediacontent.PendingFindOperation;
import org.webinos.api.mediacontent.PendingGetOperation;
import org.webinos.api.mediacontent.PendingUpdateOperation;
import org.webinos.api.mediacontent.SortMode;

import android.util.Log;

public class MediaSourceImpl implements MediaSource {

	private static final String TAG = "org.webinos.impl.MediaSourceImpl";
	
	/*
	 * MediaSource methods
	 * @see org.webinos.api.mediacontent.MediaSource#updateItem(org.webinos.api.mediacontent.MediaItem)
	 */
	
	@Override
	public void updateItem(MediaItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PendingUpdateOperation updateItemsBatch(MediaItem[] items, SuccessCallback successCallback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PendingUpdateOperation updateItemsBatch(MediaItem[] items, SuccessCallback successCallback,
			ErrorCallback errorCallback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PendingGetOperation getFolders(MediaFolderArraySuccessCallback successCallback) {
		// TODO Auto-generated method stub
		
		// http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
		
		MediaFolder mFolder = new MediaFolder();
		mFolder.folderURI = "/path/to/folder";
		mFolder.id = "12345678";
		mFolder.modifiedDate = new Date(1988, 10, 8);
		mFolder.storageType = MediaFolderStorageType.EXTERNAL;
		mFolder.title = "Marius_DummyFolder";
		
		MediaFolder[] dummyResult = {mFolder};
		
		successCallback.onsuccess(dummyResult);
		Log.e(TAG, "Dummy data sent!!!!!!!!");
		
		return null;
	}

	@Override
	public PendingGetOperation getFolders(MediaFolderArraySuccessCallback successCallback,
			ErrorCallback errorCallback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PendingFindOperation findItems(MediaItemArraySuccessCallback successCallback,
			ErrorCallback errorCallback, String folderId, AbstractFilter filter, SortMode sortMode,
			long count, long offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
