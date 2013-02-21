/********************************************************************
 * Code contributed to the webinos project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright [2013] [Marius Muntean]
 *********************************************************************/


package org.webinos.impl.mediacontent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.webinos.api.ErrorCallback;
import org.webinos.api.SuccessCallback;
import org.webinos.api.mediacontent.AbstractFilter;
import org.webinos.api.mediacontent.FilterMatchFlag;
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

import android.os.Environment;
import android.util.Log;

public class MediaSourceImpl extends MediaSource implements IModule {

	private static final String TAG = "org.webinos.impl.MediaContent.MediaSourceImpl";

	/**
	 * IModule methods
	 */
	@Override
	public Object startModule(IModuleContext ctx) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void stopModule() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * MediaSource methods
	 * 
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
	public PendingGetOperation getFolders(MediaFolderArraySuccessCallback successCallback,
			ErrorCallback errorCallback) {
		// TODO Auto-generated method stub

		// http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

		MediaFolder mFolder = MakeDummyMF();
		MediaFolder[] dummyResult = new MediaFolder[1];
		dummyResult[0] = mFolder;

//		ArrayList<File> nativeMediaFolders = new ArrayList<File>();
//		File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//		nativeMediaFolders.add(picturesFolder);
//		File musicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//		nativeMediaFolders.add(musicFolder);
//		File videosFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//		nativeMediaFolders.add(videosFolder);
//
//		for (int i = 0; i < nativeMediaFolders.size(); i++) {
//			dummyResult[i+1] = mediaFolderFromFile(nativeMediaFolders.get(i));
//		}

		successCallback.onsuccess(dummyResult);
		Log.i(TAG, "Dummy data sent!!!!!!!!");

		return null;
	}

	private MediaFolder mediaFolderFromFile(File file) {
		MediaFolder mf = new MediaFolder();
//		mf.id = UUID.randomUUID().toString();
		mf.id = "12345678";
		mf.folderURI = file.getPath();
		mf.title = file.getName();
		mf.storageType = MediaFolderStorageType.INTERNAL;
		mf.modifiedDate = new Date(file.lastModified());
		return mf;
	}

	@Override
	public PendingFindOperation findItems(MediaItemArraySuccessCallback successCallback,
			ErrorCallback errorCallback, String folderId, AbstractFilter filter, SortMode sortMode,
			long count, long offset) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Test methods
	 */
	@Override
	public String TestMethod() {
		// TODO Auto-generated method stub
		return "Test method returned succesfully!";
	}
	
	private MediaFolder MakeDummyMF(){
		MediaFolder mFolder = new MediaFolder();
		mFolder.folderURI = "/path/to/folder";
		mFolder.id = "12345678";
		mFolder.modifiedDate = new Date(1988, 10, 8);
		mFolder.storageType = MediaFolderStorageType.EXTERNAL;
		mFolder.title = "Marius_DummyFolder";
		
		return mFolder;
	}

}
