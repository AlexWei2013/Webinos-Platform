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
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.webinos.api.DeviceAPIError;
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

import android.os.AsyncTask;
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
		Thread c = Thread.currentThread();
		Log.i(TAG,"Thread ID from getFolders: "+c.getId());
		GetMediaFoldersRunnable pgot = new GetMediaFoldersRunnable(successCallback, errorCallback);
		pgot.execute(successCallback);
		return new PendingGetOperationThreaded(pgot);
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

	/**
	 * Thread implementations of some Pending-Find/Get/Update-Operation
	 */
	/*
	 * class GetMediaFoldersRunnable implements ICancelableRunnable {
	 * 
	 * MediaFolderArraySuccessCallback mfasCallback; ErrorCallback errorCallback; Boolean canceled = false;
	 * 
	 * private GetMediaFoldersRunnable(MediaFolderArraySuccessCallback mfasCallback, ErrorCallback eCallback) { this.mfasCallback = mfasCallback; this.errorCallback = eCallback; }
	 * 
	 * @Override public void run() { try { ArrayList<File> mediaFolders = new ArrayList<File>(); mediaFolders.add(Environment
	 * .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)); mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
	 * mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)); if (this.canceled) return; MediaFolder[] resultArray = new
	 * MediaFolder[mediaFolders.size()]; for (int i = 0; i < mediaFolders.size(); i++) { try { resultArray[i] = mediaFolderFromFile(mediaFolders.get(i)); } catch (Exception e) {
	 * this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR)); } if (this.canceled) return; }
	 * 
	 * this.mfasCallback.onsuccess(resultArray);
	 * 
	 * } catch (Exception e) { this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.NOT_SUPPORTED_ERR)); } }
	 * 
	 * private MediaFolder mediaFolderFromFile(File file) { // Even if the call to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC); // actually returns
	 * something, check if the folder really exists, I had to find it out the hard way. if (!file.exists()) { try { throw new Exception("File/Folder does not exist!"); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * MediaFolder mf = new MediaFolder(); mf.id = UUID.randomUUID().toString(); mf.folderURI = file.getPath(); mf.title = file.getName(); mf.storageType =
	 * MediaFolderStorageType.INTERNAL; // Careful, if the folder doesn't contain anything (like when you have a new phone) the last modified // date can cause problems, just check
	 * if the folder actually exists(regardless if Android gives you // the file object to it) and maybe if it is empty or not. long lastModified = file.lastModified();
	 * mf.modifiedDate = new Date(lastModified);
	 * 
	 * Log.i(TAG, "MediaFolder: " + mf.title + " @:" + mf.folderURI + " on:" + mf.modifiedDate.toString()); return mf; }
	 * 
	 * 
	 * @Override public void cancel() { Thread currentThread = Thread.currentThread(); currentThread.interrupt(); this.canceled = true;
	 * 
	 * }
	 * 
	 * }
	 */

	class GetMediaFoldersRunnable extends AsyncTask<MediaFolderArraySuccessCallback, Integer, MediaFolder[]>
			implements ICancelableRunnable {

		MediaFolderArraySuccessCallback mfasCallback;
		ErrorCallback errorCallback;

		private GetMediaFoldersRunnable(MediaFolderArraySuccessCallback mfasCallback, ErrorCallback eCallback) {
			this.mfasCallback = mfasCallback;
			this.errorCallback = eCallback;
		}

		private MediaFolder mediaFolderFromFile(File file) {
			// Even if the call to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			// actually returns something, check if the folder really exists, I had to find it out the hard way.
			if (!file.exists()) {
				try {
					throw new Exception("File/Folder does not exist!");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			MediaFolder mf = new MediaFolder();
			mf.id = UUID.randomUUID().toString();
			mf.folderURI = file.getPath();
			mf.title = file.getName();
			mf.storageType = MediaFolderStorageType.INTERNAL;
			// Careful, if the folder doesn't contain anything (like when you have a new phone) the last modified
			// date can cause problems, just check if the folder actually exists(regardless if Android gives you
			// the file object to it) and maybe if it is empty or not.
			long lastModified = file.lastModified();
			mf.modifiedDate = new Date(lastModified);

			Log.i(TAG,
					"MediaFolder: " + mf.title + " @:" + mf.folderURI + " on:" + mf.modifiedDate.toString());
			return mf;
		}
		private MediaFolder MakeDummyMF() {
			MediaFolder mFolder = new MediaFolder();
			mFolder.folderURI = "/path/to/folder";
			mFolder.id = "12345678";
			mFolder.modifiedDate = new Date(1988, 10, 8);
			mFolder.storageType = MediaFolderStorageType.EXTERNAL;
			mFolder.title = "Marius_DummyFolder";

			return mFolder;
		}

		@Override
		public void cancelJob() {
			Log.i(TAG, "Cancel called");
			this.cancel(true);

		}

		@Override
		protected MediaFolder[] doInBackground(MediaFolderArraySuccessCallback... params) {

			try {
				ArrayList<File> mediaFolders = new ArrayList<File>();
				mediaFolders.add(Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
				if (this.isCancelled()){
					Log.i(TAG,"Canceled");
					return null;
				}
				MediaFolder[] resultArray = new MediaFolder[mediaFolders.size()];
				for (int i = 0; i < mediaFolders.size(); i++) {
					try {
						resultArray[i] = mediaFolderFromFile(mediaFolders.get(i));
//						resultArray[i] = MakeDummyMF();
					} catch (Exception e) {
						this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
						return null;
					}
					if (this.isCancelled()){
						Log.i(TAG,"Canceled");
						return null;
				}
				}
				Thread c = Thread.currentThread();
				Log.i(TAG,"Thread ID from doInBackground: "+c.getId());
				return resultArray;
			} catch (Exception e) {
				this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.NOT_SUPPORTED_ERR));
				Log.i(TAG,"Failed while getting media folders with following error:"+e.toString());
				return null;
			}
		}

		@Override
		protected void onPostExecute(MediaFolder[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// Callback only if it wasn't interrupted
			if (result != null) {
				Thread c = Thread.currentThread();
				Log.i(TAG,"Thread ID from onPostExecute: "+c.getId());
				mfasCallback.onsuccess(result);
			}
		}

	}

	interface ICancelableRunnable {
		public void cancelJob();
	}

	class PendingGetOperationThreaded extends PendingGetOperation {

		ICancelableRunnable r;

		public PendingGetOperationThreaded(ICancelableRunnable r) {
			this.r = r;
		}

		@Override
		public void cancel() {

			if (r != null)
				r.cancelJob();

		}

	}
	
}
