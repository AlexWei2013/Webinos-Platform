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
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.webinos.api.DeviceAPIError;
import org.webinos.api.ErrorCallback;
import org.webinos.api.SuccessCallback;
import org.webinos.api.mediacontent.AbstractFilter;
import org.webinos.api.mediacontent.MediaAudio;
import org.webinos.api.mediacontent.MediaFolder;
import org.webinos.api.mediacontent.MediaFolderArraySuccessCallback;
import org.webinos.api.mediacontent.MediaFolderStorageType;
import org.webinos.api.mediacontent.MediaImage;
import org.webinos.api.mediacontent.MediaImageOrientation;
import org.webinos.api.mediacontent.MediaItem;
import org.webinos.api.mediacontent.MediaItemArraySuccessCallback;
import org.webinos.api.mediacontent.MediaItemType;
import org.webinos.api.mediacontent.MediaSource;
import org.webinos.api.mediacontent.MediaVideo;
import org.webinos.api.mediacontent.PendingFindOperation;
import org.webinos.api.mediacontent.PendingGetOperation;
import org.webinos.api.mediacontent.PendingUpdateOperation;
import org.webinos.api.mediacontent.SimpleCoordinates;
import org.webinos.api.mediacontent.SortMode;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

public class MediaSourceImpl extends MediaSource implements IModule {

	private static final String TAG = "org.webinos.impl.MediaContent.MediaSourceImpl";
	private Context androidContext;

	/**
	 * IModule methods
	 */
	@Override
	public Object startModule(IModuleContext ctx) {
		// TODO Auto-generated method stub
		this.androidContext = ((AndroidContext) ctx).getAndroidContext();
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
	public PendingUpdateOperation updateItemsBatch(MediaItem[] items, SuccessCallback successCallback,
			ErrorCallback errorCallback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PendingGetOperation getFolders(MediaFolderArraySuccessCallback successCallback,
			ErrorCallback errorCallback) {
		/*
		 * Thread c = Thread.currentThread(); Log.i(TAG, "Thread ID from getFolders: " + c.getId()); GetMediaFoldersRunnable pgot = new GetMediaFoldersRunnable(successCallback,
		 * errorCallback);
		 * 
		 * PendingGetOperationThreaded pgo = new PendingGetOperationThreaded(pgot);
		 * 
		 * pgot.execute(null);
		 * 
		 * Thread current = Thread.currentThread();
		 */

		this.SendMediaFolders(successCallback, errorCallback);
		return null;
	}

	@Override
	public PendingFindOperation findItems(MediaItemArraySuccessCallback successCallback,
			ErrorCallback errorCallback, String folderId, AbstractFilter abstractFilter, SortMode sortMode,
			long count, long offset) {
		/**
		 * FindItemsRunnable fir = new FindItemsRunnable(successCallback, errorCallback);
		 * 
		 * PendingFindOperationThreaded pfo = new PendingFindOperationThreaded(fir);
		 * 
		 * fir.execute(null);
		 */
		this.SendMediaItems(successCallback, errorCallback);
		return null;
	}

	/*
	 * Private Methods
	 */

	/**
	 * 
	 * @param file
	 * @return A MediaFolder instance representing the physical folder from the parameter
	 */
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
		try {
			mf.id = UUID.nameUUIDFromBytes(file.getName().getBytes()).toString();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			mf.id = "error";
		}
		mf.folderURI = file.getPath();
		mf.title = file.getName();
		mf.storageType = MediaFolderStorageType.INTERNAL.toString();
		// Careful
		String d = new String().valueOf(file.lastModified());
		long newFileDateMillis = (Long.parseLong(d) / 1000) * 1000;
		mf.modifiedDate = new Date(newFileDateMillis);

		Log.i(TAG, "MediaFolder: " + mf.title + " @:" + mf.folderURI + " ID: " + mf.id + " on:"
				+ mf.modifiedDate.toLocaleString() + " Type: " + mf.storageType);
		return mf;
	}

	private void SendMediaFolders(MediaFolderArraySuccessCallback successCallback, ErrorCallback errorCallback) {
		try {
			ArrayList<File> mediaFolders = new ArrayList<File>();
			mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
			mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
			mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));

			MediaFolder[] resultArray = new MediaFolder[mediaFolders.size()];
			for (int i = 0; i < mediaFolders.size(); i++) {
				try {
					resultArray[i] = mediaFolderFromFile(mediaFolders.get(i));
				} catch (Exception e) {
					errorCallback.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
					return;
				}
			}
			successCallback.onsuccess(resultArray);
			return;
		} catch (Exception e) {
			errorCallback.onerror(new DeviceAPIError(DeviceAPIError.NOT_SUPPORTED_ERR));
			Log.i(TAG, "Failed while getting media folders with following error:" + e.toString());
			return;
		}
	}

	private void SendMediaItems(MediaItemArraySuccessCallback successCallback, ErrorCallback errorCallback) {
		try {
			ArrayList<MediaItem> mediaItemsList = new ArrayList<MediaItem>();
			String[] projection = {};
			String selection = "";
			String[] selectionArgs = {};
			String sortOrder = "";
			sortOrder = MediaStore.Images.ImageColumns.SIZE + " DESC";

			ArrayList<MediaItem> imageQueryArrayList = queryMediaFolder(MediaItemType.IMAGE, projection,
					selection, selectionArgs, sortOrder);
			mediaItemsList.addAll(imageQueryArrayList);
			
//			ArrayList<MediaItem> videoQueryArrayList = queryMediaFolder(MediaItemType.VIDEO, projection,
//					selection, selectionArgs, sortOrder);
//			mediaItemsList.addAll(videoQueryArrayList);
//
//			 ArrayList<MediaItem> audioQueryArrayList = queryMediaFolder(MediaItemType.AUDIO, projection,
//			 selection, selectionArgs, sortOrder);
//			 mediaItemsList.addAll(audioQueryArrayList);

			MediaItem[] result = new MediaItem[mediaItemsList.size()];
			result = mediaItemsList.toArray(result);
			successCallback.onsuccess(result);
		} catch (Exception e) {
			errorCallback.onerror(new DeviceAPIError(DeviceAPIError.UNKNOWN_ERR));
		}
	}

	private ArrayList<MediaItem> queryMediaFolder(MediaItemType mit, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, InstantiationException, SecurityException,
			NoSuchMethodException {
		Uri locationUri;
		Class concreteMediaItemClass;
		Method concretePopulateMediaItemMethod;
		switch (mit) {
		case AUDIO: {
			locationUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			concreteMediaItemClass = MediaAudio.class;
			Class[] params = new Class[] { MediaAudio.class, Cursor.class };
			concretePopulateMediaItemMethod = MediaSourceImpl.class.getDeclaredMethod(
					"populateMediaAudioFromCursor", params);
			break;
		}
		case IMAGE: {
			locationUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			concreteMediaItemClass = MediaImage.class;
			Class[] params = new Class[] { MediaImage.class, Cursor.class };
			concretePopulateMediaItemMethod = MediaSourceImpl.class.getDeclaredMethod(
					"populateMediaImageFromCursor", params);
			break;
		}
		case VIDEO: {
			locationUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			concreteMediaItemClass = MediaVideo.class;
			Class[] params = new Class[] { MediaVideo.class, Cursor.class };
			concretePopulateMediaItemMethod = MediaSourceImpl.class.getDeclaredMethod(
					"populateMediaVideoFromCursor", params);
			break;
		}
		default: {
			locationUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			concreteMediaItemClass = MediaImage.class;
			Class[] params = new Class[] { MediaImage.class, Cursor.class };
			concretePopulateMediaItemMethod = MediaSourceImpl.class.getDeclaredMethod(
					"populateMediaImageFromCursor", params);
			break;
		}
		}
		ArrayList<MediaItem> resultList = new ArrayList<MediaItem>();
		Cursor cursor = androidContext.getContentResolver().query(locationUri, projection, selection,
				selectionArgs, sortOrder);

		if (!cursor.moveToFirst()) {
			return resultList;
		}

		while (cursor.moveToNext()) {

			MediaItem mi = (MediaItem) concreteMediaItemClass.newInstance();
			// populateMediaImageFromCursor(mi, cursor);
			concretePopulateMediaItemMethod.invoke(this, mi, cursor);
			resultList.add(mi);
		}

		cursor.close();
		cursor = null;

		return resultList;
	}

	private void populateMediaVideoFromCursor(MediaVideo mv, Cursor cursor) {
		if (cursor == null || mv == null) {
			return;
		}

		// For the inherited fields from MediaItem
		populateMediaItemFromCursor(MediaItemType.VIDEO, mv, cursor);

		// MediaVideo fields
		SimpleCoordinates sc = new SimpleCoordinates();
		sc.latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LATITUDE));
		sc.longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Video.Media.LONGITUDE));
		sc.altitude = 0;
		mv.geolocation = sc;
		mv.album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
		mv.artists = new String[] { cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)) };
		mv.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
//		mv.width = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.r)) RESOLUTION has result formatted as XxY
		mv.width = cursor.getLong(cursor.getColumnIndex("width"));
		mv.height = cursor.getLong(cursor.getColumnIndex("height"));
		mv.playedTime = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BOOKMARK));
//		mv.playCount = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.));

	}

	private void populateMediaAudioFromCursor(MediaAudio ma, Cursor cursor) {
		if (cursor == null || ma == null) {
			return;
		}

		// For the inherited fields from MediaItem
		populateMediaItemFromCursor(MediaItemType.AUDIO, ma, cursor);

		// MediaAudio fields
		ma.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		// ma.genres = new String[] { cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)) };
		ma.artists = new String[] { cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) };
		ma.composers = new String[] { cursor
				.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)) };
		// ma.lyrics ????????
		// ma.copyright ????????
		// ma.bitrate = Compute as described here: http://stackoverflow.com/questions/5140085/how-to-get-sampling-rate-and-frequency-of-music-file-mp3-in-android
		ma.trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
		ma.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		// ma.playCount =
		ma.playedTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));

	}

	private void populateMediaImageFromCursor(MediaImage mi, Cursor cursor) {
		if (cursor == null || mi == null) {
			return;
		}

		// For the inherited fields from MediaItem
		populateMediaItemFromCursor(MediaItemType.IMAGE, mi, cursor);

		// MediaImage fields
		SimpleCoordinates sc = new SimpleCoordinates();
		sc.latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE));
		sc.longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE));
		sc.altitude = 0;
		mi.geolocation = sc;
		mi.width = cursor.getLong(cursor.getColumnIndex("width"));
		mi.height = cursor.getLong(cursor.getColumnIndex("height"));

		/**
		 * From the documentation: The orientation for the image expressed as degrees. Only degrees 0, 90, 180, 270 will work.
		 */
		int imgOrientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
		MediaImageOrientation mio;
		switch (imgOrientation) {
		case 0: {
			mio = MediaImageOrientation.NORMAL;
			break;
		}
		case 90: {
			mio = MediaImageOrientation.ROTATE_90;
			break;
		}
		case 180: {
			mio = MediaImageOrientation.FLIP_VERTICAL;
			break;
		}
		case 270: {
			mio = MediaImageOrientation.ROTATE_270;
			break;
		}
		default: {
			mio = MediaImageOrientation.UNDEFINED;
			break;
		}
		}

		mi.orientation = mio.toString();
	}

	private void populateMediaItemFromCursor(MediaItemType mit, MediaItem mi, Cursor cursor) {

		String id = null;
		String title = null;
		String uri = null;
		String dateTaken = null;
		String dateModified = null;
		String size = null;
		String desc = null;
		String rating = null;
		switch (mit) {
		case AUDIO: {
			id = MediaStore.Audio.Media._ID;
			title = MediaStore.Audio.Media.TITLE;
			uri = MediaStore.Audio.Media.DATA;
			dateTaken = MediaStore.Audio.Media.DATE_ADDED;
			dateModified = MediaStore.Audio.Media.DATE_MODIFIED;
			size = MediaStore.Audio.Media.SIZE;
			break;
		}
		case IMAGE: {
			id = MediaStore.Images.Media._ID;
			title = MediaStore.Images.Media.TITLE;
			uri = MediaStore.Images.Media.DATA;
			dateTaken = MediaStore.Images.Media.DATE_TAKEN;
			dateModified = MediaStore.Images.Media.DATE_MODIFIED;
			size = MediaStore.Images.Media.SIZE;
			desc = MediaStore.Images.Media.DESCRIPTION;
			break;
		}
		case VIDEO: {
			id = MediaStore.Video.Media._ID;
			title = MediaStore.Video.Media.TITLE;
			uri = MediaStore.Video.Media.DATA;
			dateTaken = MediaStore.Video.Media.DATE_ADDED;
			dateModified = MediaStore.Video.Media.DATE_MODIFIED;
			size = MediaStore.Video.Media.SIZE;
			desc = MediaStore.Video.Media.DESCRIPTION;
			break;
		}
		default: {

			break;
		}
		}

		mi.editableAttributes = new String[] { "title" };
		mi.id = cursor.getString(cursor.getColumnIndex(id));
		mi.title = cursor.getString(cursor.getColumnIndex(title));
		mi.itemURI = cursor.getString(cursor.getColumnIndex(uri));
		mi.releaseDate = new Date(cursor.getLong(cursor.getColumnIndex(dateTaken)) * 1000);
		mi.modifiedDate = new Date(cursor.getLong(cursor.getColumnIndex(dateModified)) * 1000);
		mi.mimeType = mit.toString().substring(0, 1).toUpperCase()
				+ mit.toString().toLowerCase().substring(1);
		mi.type = mit.toString();
		mi.size = cursor.getLong(cursor.getColumnIndex(size));
		if (desc == null) {
			mi.description = "no description";
		} else {
			String description = cursor.getString(cursor.getColumnIndex(desc));
			mi.description = (description == null ? "no description"
					: (description.isEmpty() ? "empty description" : description));
		}
		// mi.rating = 7;
	}

	// class GetMediaFoldersRunnable extends AsyncTask<MediaFolderArraySuccessCallback, Void, MediaFolder[]>
	// implements ICancelableRunnable {
	//
	// MediaFolderArraySuccessCallback mfasCallback;
	// ErrorCallback errorCallback;
	//
	// private GetMediaFoldersRunnable(MediaFolderArraySuccessCallback mfasCallback, ErrorCallback eCallback) {
	// this.mfasCallback = mfasCallback;
	// this.errorCallback = eCallback;
	// }
	//
	// private MediaFolder mediaFolderFromFile(File file) {
	// // Even if the call to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
	// // actually returns something, check if the folder really exists, I had to find it out the hard way.
	// if (!file.exists()) {
	// try {
	// throw new Exception("File/Folder does not exist!");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// MediaFolder mf = new MediaFolder();
	// try {
	// mf.id = UUID.nameUUIDFromBytes(file.getName().getBytes()).toString();
	// } catch (Exception e) {
	// Log.e(TAG, e.getMessage());
	// mf.id = "error";
	// }
	// mf.folderURI = file.getPath();
	// mf.title = file.getName();
	// mf.storageType = MediaFolderStorageType.INTERNAL.toString();
	// // Careful
	// String d = new String().valueOf(file.lastModified());
	// long newFileDateMillis = (Long.parseLong(d) / 1000) * 1000;
	// mf.modifiedDate = new Date(newFileDateMillis);
	//
	// Log.i(TAG, "MediaFolder: " + mf.title + " @:" + mf.folderURI + " ID: " + mf.id + " on:"
	// + mf.modifiedDate.toLocaleString() + " Type: " + mf.storageType);
	// return mf;
	// }
	//
	// private MediaFolder MakeDummyMF(File file) {
	// MediaFolder mFolder = new MediaFolder();
	// mFolder.folderURI = "/path/to/folder";
	// mFolder.id = UUID.randomUUID().toString();
	// long fileDate = file.lastModified();
	// long systemDate = System.currentTimeMillis();
	//
	// mFolder.modifiedDate = new Date(systemDate);
	// mFolder.storageType = MediaFolderStorageType.EXTERNAL.toString();
	// mFolder.title = "Marius_DummyFolder";
	//
	// return mFolder;
	// }
	//
	// @Override
	// public void cancelJob() {
	// Log.i(TAG, "Cancel called");
	// this.cancel(true);
	//
	// }
	//
	// @Override
	// protected MediaFolder[] doInBackground(MediaFolderArraySuccessCallback... params) {
	//
	// try {
	// ArrayList<File> mediaFolders = new ArrayList<File>();
	// mediaFolders.add(Environment
	// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
	// mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
	// mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
	// if (this.isCancelled()) {
	// Log.i(TAG, "Canceled");
	// return null;
	// }
	// MediaFolder[] resultArray = new MediaFolder[mediaFolders.size()];
	// for (int i = 0; i < mediaFolders.size(); i++) {
	// try {
	// resultArray[i] = mediaFolderFromFile(mediaFolders.get(i));
	// } catch (Exception e) {
	// this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
	// return null;
	// }
	// if (this.isCancelled()) {
	// Log.i(TAG, "Canceled");
	// return null;
	// }
	// }
	// // Thread c = Thread.currentThread();
	// // Log.i(TAG, "Thread ID from doInBackground: " + c.getId());
	// // params[0].onsuccess(resultArray);
	// return resultArray;
	// } catch (Exception e) {
	// this.errorCallback.onerror(new DeviceAPIError(DeviceAPIError.NOT_SUPPORTED_ERR));
	// Log.i(TAG, "Failed while getting media folders with following error:" + e.toString());
	// return null;
	// }
	// }
	//
	// @Override
	// protected void onPostExecute(MediaFolder[] result) {
	// // TODO Auto-generated method stub
	// super.onPostExecute(result);
	// // Callback only if it wasn't interrupted
	// if (result != null) {
	// // Thread c = Thread.currentThread();
	// // Log.i(TAG, "Thread ID from onPostExecute: " + c.getId());
	// mfasCallback.onsuccess(result);
	// }
	// }
	//
	// }
	//
	// class FindItemsRunnable extends AsyncTask<MediaItemArraySuccessCallback, Void, MediaItem[]> implements
	// ICancelableRunnable {
	//
	// MediaItemArraySuccessCallback miasCallback;
	// ErrorCallback errorCallback;
	//
	// private FindItemsRunnable(MediaItemArraySuccessCallback miasCallback, ErrorCallback errorCallback) {
	// this.miasCallback = miasCallback;
	// this.errorCallback = errorCallback;
	// }
	//
	// @Override
	// public void cancelJob() {
	// Log.i(TAG, "Cancel called");
	// this.cancel(true);
	// }
	//
	// @Override
	// protected MediaItem[] doInBackground(MediaItemArraySuccessCallback... params) {
	// ArrayList<MediaItem> mediaItemsList = new ArrayList<MediaItem>();
	//
	// String[] projection = {};
	// String selection = "";
	// String[] selectionArgs = {};
	// String sortOrder = "";
	//
	// Cursor cursor = androidContext.getContentResolver().query(
	// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs,
	// sortOrder);
	//
	// if (!cursor.moveToFirst()) {
	// return null;
	// }
	//
	// while (cursor.moveToNext()) {
	// MediaItem mi = new MediaItem();
	// mi.id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
	// mi.title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
	// mi.itemURI = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
	// String strMillis = String.valueOf((cursor.getLong(cursor
	// .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))));
	// long longMillis = (Long.parseLong(strMillis) / 1000) * 1000;
	// mi.releaseDate = new Date(longMillis);
	// // mi.modifiedDate = new Date(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
	// mi.mimeType = "Image";
	// mi.type = MediaItemType.IMAGE.toString();
	// mi.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
	//
	// mediaItemsList.add(mi);
	// }
	// cursor = null;
	//
	// MediaItem[] result = new MediaItem[mediaItemsList.size()];
	// result = mediaItemsList.toArray(result);
	//
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(MediaItem[] result) {
	// // TODO Auto-generated method stub
	// super.onPostExecute(result);
	// // Callback only if it wasn't interrupted
	// if (result != null) {
	// // Thread c = Thread.currentThread();
	// // Log.i(TAG, "Thread ID from onPostExecute: " + c.getId());
	// this.miasCallback.onsuccess(result);
	// }
	// }
	//
	// }
	//
	// interface ICancelableRunnable {
	// public void cancelJob();
	// }
	//
	// class PendingGetOperationThreaded extends PendingGetOperation {
	//
	// ICancelableRunnable r;
	//
	// public PendingGetOperationThreaded(ICancelableRunnable r) {
	// this.r = r;
	// }
	//
	// @Override
	// public void cancel() {
	//
	// if (r != null)
	// r.cancelJob();
	//
	// }
	//
	// }
	//
	// class PendingFindOperationThreaded extends PendingFindOperation {
	//
	// ICancelableRunnable r;
	//
	// public PendingFindOperationThreaded(ICancelableRunnable r) {
	// this.r = r;
	// }
	//
	// @Override
	// public void cancel() {
	//
	// if (r != null)
	// r.cancelJob();
	//
	// }
	//
	// }

}
