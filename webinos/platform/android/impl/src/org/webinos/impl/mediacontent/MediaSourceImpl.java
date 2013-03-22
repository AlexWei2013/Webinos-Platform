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

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.meshpoint.anode.AndroidContext;
import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.webinos.api.DeviceAPIError;
import org.webinos.api.ErrorCallback;
import org.webinos.api.SuccessCallback;
import org.webinos.api.mediacontent.AbstractFilter;
import org.webinos.api.mediacontent.AttributeFilter;
import org.webinos.api.mediacontent.AttributeRangeFilter;
import org.webinos.api.mediacontent.CompositeFilter;
import org.webinos.api.mediacontent.CompositeFilterType;
import org.webinos.api.mediacontent.FilterMatchFlag;
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
import org.webinos.api.mediacontent.SortModeOrder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class MediaSourceImpl extends MediaSource implements IModule {

	private static final String TAG = "org.webinos.impl.MediaContent.MediaSourceImpl";
	private Context androidContext;
	private HashMap<String, String> filterMatchFlagToSqlOperatorMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6849629792064412403L;

		{
			put(FilterMatchFlag.EXACTLY.toString(), " = ");
			put(FilterMatchFlag.FULLSTRING.toString(), " like ");
			put(FilterMatchFlag.CONTAINS.toString(), " like ");
			put(FilterMatchFlag.STARTSWITH.toString(), " like ");
			put(FilterMatchFlag.ENDSWITH.toString(), " like ");
			/**
			 * Separate query for table columns: PRAGMA table_info(tablename); and then go through all of them checking whether the needed attribute(i.e. column name) exists.
			 * 
			 * Beware! For now EXISTS is ignored.
			 */
			// put(FilterMatchFlag.EXISTS.toString(),"???);

		}
	};

	private HashMap<String, String> attrNameToImageColumnNameMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -176270944529455326L;

		{
			put("id", MediaStore.Images.Media._ID);
			put("title", MediaStore.Images.Media.TITLE);
			put("itemURI", MediaStore.Images.Media.DATA);
			put("releaseDate", MediaStore.Images.Media.DATE_TAKEN);
			put("modifiedDate", MediaStore.Images.Media.DATE_MODIFIED);
			put("size", MediaStore.Images.Media.SIZE);
			put("description", MediaStore.Images.Media.DESCRIPTION);
			put("latitude", MediaStore.Images.Media.LATITUDE);
			put("longitude", MediaStore.Images.Media.LONGITUDE);
			// put("width", MediaStore.Images.Media.WIDTH);
			// put("width", MediaStore.Images.Media.HEIGHT);
			put("orientation", MediaStore.Images.Media.ORIENTATION);
		}
	};

	private HashMap<String, String> attrNameToVideoColumnNameMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8736202366138949678L;

		{
			put("id", MediaStore.Video.Media._ID);
			put("title", MediaStore.Video.Media.TITLE);
			put("itemURI", MediaStore.Video.Media.DATA);
			put("releaseDate", MediaStore.Video.Media.DATE_TAKEN);
			put("modifiedDate", MediaStore.Video.Media.DATE_MODIFIED);
			put("size", MediaStore.Video.Media.SIZE);
			put("description", MediaStore.Video.Media.DESCRIPTION);
			put("latitude", MediaStore.Video.Media.LATITUDE);
			put("longitude", MediaStore.Video.Media.LONGITUDE);
			put("album", MediaStore.Video.Media.ALBUM);
			put("artist", MediaStore.Video.Media.ARTIST);
			put("duration", MediaStore.Video.Media.DURATION);
			// put("width", MediaStore.Video.Media.WIDTH);
			// put("width", MediaStore.Video.Media.HEIGHT);
			put("resolution", MediaStore.Video.Media.RESOLUTION);
			put("playedTime", MediaStore.Video.Media.BOOKMARK);
			// put("playedCount", MediaStore.Video.Media.???);
		}
	};

	private HashMap<String, String> attrNameToAudioColumnNameMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5487498224217244507L;

		{
			put("id", MediaStore.Audio.Media._ID);
			put("title", MediaStore.Audio.Media.TITLE);
			put("itemURI", MediaStore.Audio.Media.DATA);
			put("releaseDate", MediaStore.Audio.Media.DATE_ADDED);
			put("modifiedDate", MediaStore.Audio.Media.DATE_MODIFIED);
			put("size", MediaStore.Audio.Media.SIZE);
			put("album", MediaStore.Audio.Media.ALBUM);
			// put("genre", MediaStore.Audio.Media.???);
			put("artist", MediaStore.Audio.Media.ARTIST);
			put("composer", MediaStore.Audio.Media.COMPOSER);
			put("trackNumber", MediaStore.Audio.Media.TRACK);
			// put("lyrics", MediaStore.Audio.Media.???);
			// put("copyright", MediaStore.Audio.Media.???);
			put("duration", MediaStore.Audio.Media.DURATION);
			// put("bitrate", MediaStore.Audio.Media.???);
			// put("playedCount", MediaStore.Audio.Media.???);
			put("playedTime", MediaStore.Audio.Media.BOOKMARK);
		}
	};

	private HashMap<String, String> folderIDtoPathMapping = null;
	
	private PendingGetOperationImpl pgoImpl = null;
	private PendingFindOperationImpl pfoImpl = null;
	/**
	 * IModule methods
	 */
	@Override
	public Object startModule(IModuleContext ctx) {
		// TODO Auto-generated method stub
		// populateFolderIdToPathMapping();
		this.androidContext = ((AndroidContext) ctx).getAndroidContext();
		populateFolderIdToPathMapping();
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
		presistMediaItem(item);
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
		if(this.folderIDtoPathMapping == null)
			populateFolderIdToPathMapping();
		
		if(this.pgoImpl == null)
			this.pgoImpl = new PendingGetOperationImpl();
		
		return this.pgoImpl.Get(successCallback, errorCallback, folderIDtoPathMapping);
	}

	@Override
	public PendingFindOperation findItems(MediaItemArraySuccessCallback successCallback,
			ErrorCallback errorCallback, String folderId, AbstractFilter abstractFilter, SortMode sortMode,
			Integer count, Integer offset) {

	

		if(this.folderIDtoPathMapping == null)
			populateFolderIdToPathMapping();
			
		if(this.pfoImpl == null){
			this.pfoImpl = new PendingFindOperationImpl();
		}
		
		return this.pfoImpl.Get(successCallback, errorCallback, androidContext, folderIDtoPathMapping, folderId, abstractFilter, sortMode, count, offset);
		
	}
	

	/*
	 * Private Methods
	 */

	private void presistMediaItem(MediaItem mi) {
		if (mi == null)
			return;

		Uri location = null;
		ContentValues updatedValues = new ContentValues();
		String selectionClause = null;

		MediaItemType mit = MediaItemType.valueOf(mi.type);
		switch (mit) {
		case AUDIO: {
			location = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

			MediaAudio ma = (MediaAudio) mi;
			updatedValues.put(MediaStore.Audio.Media.TITLE_KEY, ma.title);
			updatedValues.put(MediaStore.Audio.Media.ALBUM, ma.album);
			updatedValues.put(MediaStore.Audio.Media.ARTIST, ma.artists[0]);
			updatedValues.put(MediaStore.Audio.Media.COMPOSER, ma.composers[0]);
			updatedValues.put(MediaStore.Audio.Media.TRACK, ma.trackNumber);
			updatedValues.put(MediaStore.Audio.Media.BOOKMARK, ma.playedTime);

			selectionClause = attrNameToAudioColumnNameMapping.get("itemURI") + " like ?";
			break;
		}
		case IMAGE: {
			location = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

			MediaImage mImg = (MediaImage) mi;
			updatedValues.put(MediaStore.Images.Media.TITLE, mImg.title);
			updatedValues.put(MediaStore.Images.Media.DESCRIPTION, mImg.description);
			updatedValues.put(MediaStore.Images.Media.LATITUDE, mImg.geolocation.latitude);
			updatedValues.put(MediaStore.Images.Media.LONGITUDE, mImg.geolocation.longitude);

			String orient = getOrientationFromMediaImageOrientation(mImg.orientation);
			updatedValues.put(MediaStore.Images.Media.ORIENTATION, orient);

			selectionClause = attrNameToImageColumnNameMapping.get("itemURI") + " like ?";
			break;
		}
		case VIDEO: {
			location = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

			MediaVideo mv = (MediaVideo) mi;
			updatedValues.put(MediaStore.Video.Media.TITLE, mv.title);
			updatedValues.put(MediaStore.Video.Media.DESCRIPTION, mv.description);
			updatedValues.put(MediaStore.Video.Media.LATITUDE, mv.geolocation.latitude);
			updatedValues.put(MediaStore.Video.Media.LONGITUDE, mv.geolocation.longitude);
			updatedValues.put(MediaStore.Video.Media.ALBUM, mv.album);
			updatedValues.put(MediaStore.Video.Media.ARTIST, mv.artists[0]);
			updatedValues.put(MediaStore.Video.Media.BOOKMARK, mv.playedTime);

			selectionClause = attrNameToVideoColumnNameMapping.get("itemURI") + " like ?";
			break;
		}
		default: {
			return;
		}
		}

		String[] selectionArgs = new String[] { mi.itemURI };
		int rowsUpdated = 0;
		try {
			rowsUpdated = androidContext.getContentResolver().update(location, updatedValues,
					selectionClause, selectionArgs);
		} catch (Exception e) {
			// new DeviceAPIError(DeviceAPIError.INVALID_VALUES_ERR);
		}
		Log.i(TAG, "UpdateItems - Number of affected rows: " + rowsUpdated);

		if (rowsUpdated == 0) {
			// Do something
		}

	}

	private String getOrientationFromMediaImageOrientation(String miOrientation) {
		if (miOrientation == null || miOrientation.isEmpty())
			return null;

		String orient = null;
		switch (MediaImageOrientation.valueOf(miOrientation)) {
		case NORMAL: {
			orient = String.valueOf(0);
			break;
		}
		case ROTATE_180: {
			orient = String.valueOf(180);
			break;
		}
		case ROTATE_270: {
			orient = String.valueOf(270);
			break;
		}
		case ROTATE_90: {
			orient = String.valueOf(90);
			break;
		}
		default: {
			orient = String.valueOf(0);
		}
		}

		return orient;
	}

	/**
	 * Populates a HashMap
	 */
	private void populateFolderIdToPathMapping() {
		folderIDtoPathMapping = new HashMap<String, String>() {

			private static final long serialVersionUID = -6084664999740353290L;

			{
				put(UUID.nameUUIDFromBytes(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
								.getName().getBytes()).toString(), Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());

				put(UUID.nameUUIDFromBytes(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getName()
								.getBytes()).toString(),
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath());

				put(UUID.nameUUIDFromBytes(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getName()
								.getBytes()).toString(),
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());

				put(UUID.nameUUIDFromBytes(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName()
								.getBytes()).toString(),
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());

				put(UUID.nameUUIDFromBytes(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
								.getName().getBytes()).toString(), Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
			}
		};
	}

}