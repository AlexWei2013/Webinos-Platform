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
import java.io.InvalidClassException;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
	private HashMap<String, String> filterMatchFlagToSqlOperatorMapping = new HashMap<String, String>() {
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

	/**
	 * IModule methods
	 */
	@Override
	public Object startModule(IModuleContext ctx) {
		// TODO Auto-generated method stub
//		populateFolderIdToPathMapping();
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
			Integer count, Integer offset) {
		/**
		 * FindItemsRunnable fir = new FindItemsRunnable(successCallback, errorCallback);
		 * 
		 * PendingFindOperationThreaded pfo = new PendingFindOperationThreaded(fir);
		 * 
		 * fir.execute(null);
		 */
		this.SendMediaItems(successCallback, errorCallback, folderId, abstractFilter, sortMode, count, offset);
		return null;
	}

	/*
	 * Private Methods
	 */

	private void populateFolderIdToPathMapping() {
		folderIDtoPathMapping = new HashMap<String, String>() {

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

		// Add the folder ID and path to the mapping. This is used in searches when a folder ID is provided.
		if (folderIDtoPathMapping != null)
			folderIDtoPathMapping.put(mf.id, mf.folderURI);

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
			mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
			mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

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

	private void SendMediaItems(MediaItemArraySuccessCallback successCallback, ErrorCallback errorCallback,
			String folderId, AbstractFilter abstractFilter, SortMode sortMode, Integer count, Integer offset) {
		try {
			ArrayList<MediaItem> mediaItemsList = new ArrayList<MediaItem>();

			ArrayList<MediaItem> imageQueryArrayList = queryMediaFolder(MediaItemType.IMAGE, folderId,
					abstractFilter, sortMode, count, offset);
			mediaItemsList.addAll(imageQueryArrayList);

			ArrayList<MediaItem> videoQueryArrayList = queryMediaFolder(MediaItemType.VIDEO, folderId,
					abstractFilter, sortMode, count, offset);
			mediaItemsList.addAll(videoQueryArrayList);

			ArrayList<MediaItem> audioQueryArrayList = queryMediaFolder(MediaItemType.AUDIO, folderId,
					abstractFilter, sortMode, count, offset);
			mediaItemsList.addAll(audioQueryArrayList);

			MediaItem[] result = new MediaItem[mediaItemsList.size()];
			result = mediaItemsList.toArray(result);
			successCallback.onsuccess(result);
		} catch (Exception e) {
			errorCallback.onerror(new DeviceAPIError(DeviceAPIError.INVALID_VALUES_ERR));
		}
	}

	private String getSelectionFromFilter(MediaItemType mit, AbstractFilter filter, String folderID)
			throws InvalidClassException {
		if (filter == null)
			return null;

		String locationSelection = getSelectionFromFolderID(mit, folderID);

		String attributesSelection = null;
		Class filterClass = filter.getClass();
		// Ugly if-else but switch doesn't work like in C#
		if (filterClass.equals(CompositeFilter.class)) {
			attributesSelection = getSelectionFromCompositeFilter(mit, (CompositeFilter) filter);
		} else {
			if (filterClass.equals(AttributeFilter.class)) {
				attributesSelection = getSelectionFromAttributeFilter(mit, (AttributeFilter) filter);
			} else {
				if (filterClass.equals(AttributeRangeFilter.class)) {
					attributesSelection = getSelectionFromAttributeRangeFilter(mit,
							(AttributeRangeFilter) filter);
				} else {
					throw new InvalidClassException(filterClass.getName() + " is not supported!");
				}
			}
		}
		StringBuilder selection = new StringBuilder();
		if (locationSelection != null) {
			selection.append(locationSelection);
			if (attributesSelection != null) {
				selection.append(" AND ");
				selection.append(attributesSelection);
			}
		} else {
			if (attributesSelection != null) {
				selection.append(attributesSelection);
			}
		}

		Log.i(TAG, "Selection: " + selection.toString());
		if (selection.length() <= 0)
			return null;

		return selection.toString();
	}

	private String getSelectionFromFolderID(MediaItemType mit, String folderID) {
		if (folderID == null || folderID.isEmpty())
			return null;

		String realPath = folderIDtoPathMapping.get(folderID);
		if (realPath == null)
			return null;

		String pathColName = null;

		switch (mit) {
		case AUDIO: {
			pathColName = attrNameToAudioColumnNameMapping.get("itemURI");
			break;
		}
		case IMAGE: {
			pathColName = attrNameToImageColumnNameMapping.get("itemURI");
			break;
		}
		case VIDEO: {
			pathColName = attrNameToVideoColumnNameMapping.get("itemURI");
			break;
		}
		default: {
			return null;
		}
		}
		if (pathColName == null || pathColName.isEmpty())
			return null;

		// Example: ... WHERE _data like /storage/emulated/0/Music% AND ...
		StringBuilder sb = new StringBuilder();
		sb.append(pathColName);
		sb.append(" like ");
		sb.append("'");
		sb.append(realPath);
		sb.append("%");
		sb.append("'");
		// " AND " is added in the calling method only if necessary.
		// sb.append(" AND ");

		return sb.toString();
	}

	private String getSelectionFromAttributeRangeFilter(MediaItemType mit, AttributeRangeFilter arFilter) {
		if (arFilter == null || arFilter.attributeName == null || arFilter.attributeName.isEmpty())
			return null;

		// Determine the correct column name based on the MediaItemType
		String colName = null;
		switch (mit) {
		case AUDIO: {
			colName = attrNameToAudioColumnNameMapping.get((arFilter).attributeName);
			break;
		}
		case IMAGE: {
			colName = attrNameToImageColumnNameMapping.get((arFilter).attributeName);
			break;
		}
		case VIDEO: {
			colName = attrNameToVideoColumnNameMapping.get((arFilter).attributeName);
			break;
		}
		default: {
			break;
		}
		}
		// If no valid column name found just return
		if (colName == null || colName.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String minVal = null, maxVal = null;
		// Special check for Date objects, in the SQLite DB dates are stored like seconds since the epoch.
		// To make values compatible, divide by 1000 (see getTime() documentation);
		if (arFilter.attributeName.contains("date")) {
			if (arFilter.initialValue != null) {
				minVal = String.valueOf(((Date) arFilter.initialValue).getTime() / 1000);
			}
			if (arFilter.endValue != null) {
				maxVal = String.valueOf(((Date) arFilter.endValue).getTime() / 1000);
			}
		} else {
			if (arFilter.initialValue != null) {
				minVal = String.valueOf(arFilter.initialValue);
			}
			if (arFilter.endValue != null) {
				maxVal = String.valueOf(arFilter.endValue);
			}
		}

		if (minVal != null) {
			sb.append(colName + " >= " + minVal);
		}
		if (maxVal != null) {
			// If lower bound is set we need and "AND"
			if (minVal != null) {
				sb.append(" AND " + colName + " <= " + maxVal);
			} else {
				// Otherwise we don't
				sb.append(colName + " <= " + maxVal);
			}
		}

		if (minVal == null && maxVal == null) {
			return null;
		} else {
			// Log.i(TAG, "Attribute range selection: " + sb.toString());
			return sb.toString();
		}
	}

	private String getSelectionFromAttributeFilter(MediaItemType mit, AttributeFilter aFilter) {
		if (aFilter == null || aFilter.attributeName == null || aFilter.attributeName.isEmpty())
			return null;

		String columnName = null;
		switch (mit) {
		case AUDIO: {
			columnName = attrNameToAudioColumnNameMapping.get(((AttributeFilter) aFilter).attributeName);
		}
		case IMAGE: {
			columnName = attrNameToImageColumnNameMapping.get(((AttributeFilter) aFilter).attributeName);
		}
		case VIDEO: {
			columnName = attrNameToVideoColumnNameMapping.get(((AttributeFilter) aFilter).attributeName);
		}
		default: {
			break;
		}
		}

		// If attribute name was not found skip this filter
		if (columnName == null || columnName.isEmpty())
			return null;

		StringBuilder sb = new StringBuilder();
		try {
			String matchFlag = aFilter.filterMatchFlag;
			if (!matchFlag.toUpperCase().equals("EXISTS")) {
				// Column name
				sb.append(columnName);
				// Operator "=" or "like"
				String operator = filterMatchFlagToSqlOperatorMapping.get(matchFlag);
				sb.append(operator);
				// Placeholder for actual value is "?"
				sb.append("?");
			} else {
				return null;
			}
		} catch (Exception e) {
			Log.i(TAG, "Exception occured while getting SQLite selection from an attributeFilter obj.");
			sb = null;
		}

		return sb.toString();
	}

	private String getSelectionFromCompositeFilter(MediaItemType mit, CompositeFilter cFilter) {
		if (cFilter == null || cFilter.filters == null || cFilter.filters.length == 0) {
			return null;
		}

		String selType;// UNION or INTERSECTION i.e. "OR" or "AND"
		selType = cFilter.CompositeFilterType.toUpperCase().equals("UNION") ? " OR " : " AND ";

		StringBuilder sb = new StringBuilder();
		for (AbstractFilter aFilter : cFilter.filters) {
			String currentSelection = null;

			if (aFilter.getClass().equals(AttributeFilter.class)) {
				currentSelection = getSelectionFromAttributeFilter(mit, (AttributeFilter) aFilter);
				if (currentSelection != null) {
					sb.append(currentSelection);
					sb.append(selType);
				}
			} else {
				if (aFilter.getClass().equals(AttributeRangeFilter.class)) {
					currentSelection = getSelectionFromAttributeRangeFilter(mit,
							(AttributeRangeFilter) aFilter);
					if (currentSelection != null) {
						sb.append(currentSelection);
						sb.append(selType);
					}
				}
			}
		}
		// If the composite filter didn't contain any valid filter, return null.
		if (sb.toString().length() <= 0) {
			return null;
		}
		// If the composite filter contained at least one valid filter, the last " OR " or " AND " should be deleted.
		// Here I'm computing the position where it starts so as to avoid it.
		int offset = selType.equals(" OR ") ? 4 : 5;
		int length = sb.toString().length();
		// Omit returning last OR or AND.
		String selection = sb.toString().substring(0, length - offset); // TODO make sure (length - offset) > 0
		// Log.i(TAG, "Selection from composite filter: " + selection);
		return selection;

	}

	private String[] getSelectionArgsFromFilter(AbstractFilter filter) {
		CompositeFilter cFilter;
		try {
			cFilter = (CompositeFilter) filter;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
			return new String[] {};
		}
		ArrayList<String> selectionArgs = new ArrayList<String>();
		for (AbstractFilter aFilter : cFilter.filters) {
			try {
				// selectionArgs.add((String) ((AttributeFilter) aFilter).matchValue);
				selectionArgs.add(getSelArgFromAttrFilter((AttributeFilter) aFilter));
				// Log.i(TAG, "Selection arg: "+selectionArgs.get(selectionArgs.size()));
			} catch (Exception e) {
			}
		}
		String[] selectionArgsArr = new String[selectionArgs.size()];
		return selectionArgs.toArray(selectionArgsArr);
	}

	private String getSelArgFromAttrFilter(AttributeFilter aFilter) {

		if (aFilter == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		try {
			switch (FilterMatchFlag.valueOf(aFilter.filterMatchFlag)) {
			case EXACTLY: {
				sb.append(aFilter.matchValue);
				break;
			}
			case FULLSTRING: {
				sb.append(aFilter.matchValue);
				break;
			}
			case CONTAINS: {
				sb.append("%");
				sb.append(aFilter.matchValue);
				sb.append("%");
				break;
			}
			case STARTSWITH: {
				sb.append("%");
				sb.append(aFilter.matchValue);
				break;
			}
			case ENDSWITH: {
				sb.append(aFilter.matchValue);
				sb.append("%");
				break;
			}
			default: {
				sb.append(aFilter.matchValue);
				break;
			}
			}
		} catch (Exception e) {
			Log.i(TAG, "Error in SelArgFromFilter: " + e.toString());
			sb.append(aFilter.matchValue);
		}

		// Log.i(TAG, "SelArg: " + sb.toString());
		return sb.toString();

	}

	private String getSortModeCountOffsetString(MediaItemType mit, SortMode sortMode, Integer count,
			Integer offset) {
		if (sortMode == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		// Determine the column name to sort by
		String colName = null;
		switch (mit) {
		case IMAGE: {
			colName = attrNameToImageColumnNameMapping.get(sortMode.attributeName);
			break;
		}
		case AUDIO: {
			colName = attrNameToAudioColumnNameMapping.get(sortMode.attributeName);
			break;
		}
		case VIDEO: {
			colName = attrNameToVideoColumnNameMapping.get(sortMode.attributeName);
			break;
		}
		}
		if (colName != null && !colName.isEmpty()) {
			sb.append(" " + colName);
		}

		// Sort mode
		String ascOrDesc = sortMode.order.toUpperCase().equals("DESC") ? " DESC" : " ASC";
		sb.append(ascOrDesc);

		// Limit the nr. of results
		if (count != null) {
			sb.append(" limit " + count.intValue());
		}
		// Set an offset
		if (offset != null) {
			sb.append(" offset " + offset.intValue());
		}
		Log.i(TAG, "Sort mode, count and offset:" + sb.toString());
		return sb.toString();
	}

	private ArrayList<MediaItem> queryMediaFolder(MediaItemType mit, String folderId,
			AbstractFilter abstractFilter, SortMode sortMode, Integer count, Integer offset)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			InstantiationException, SecurityException, NoSuchMethodException {

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

		String[] projection = {};
		String selection = "";
		String[] selectionArgs = {};
		String sortOrder = "";

		// For testing
		AttributeFilter af = new AttributeFilter();
		af.attributeName = MediaStore.MediaColumns.TITLE;
		af.filterMatchFlag = FilterMatchFlag.CONTAINS.toString();
		af.matchValue = "img";

		AttributeFilter af2 = new AttributeFilter();
		af2.attributeName = MediaStore.MediaColumns.TITLE;
		af2.filterMatchFlag = FilterMatchFlag.CONTAINS.toString();
		af2.matchValue = "t";

		AttributeRangeFilter ar = new AttributeRangeFilter();
		ar.attributeName = "size";
		ar.initialValue = 200000;
		ar.endValue = 4096000;

		CompositeFilter cf = new CompositeFilter();
		cf.CompositeFilterType = CompositeFilterType.INTERSECTION.toString();
		cf.filters = new AbstractFilter[] { af, ar };

		abstractFilter = cf;
		folderId = "c3418983-f961-37fb-9950-49c67426dc08"; // pics folder

		try {
			selection = getSelectionFromFilter(mit, abstractFilter, folderId);
			selectionArgs = getSelectionArgsFromFilter(abstractFilter);
		} catch (Exception e) {
			selection = null;
			selectionArgs = null;
		}

		SortMode tempSortMode = new SortMode();
		tempSortMode.attributeName = "size";
		tempSortMode.order = SortModeOrder.DESC.toString();
		sortOrder = getSortModeCountOffsetString(mit, tempSortMode, null, null);
		// end For testing

		ArrayList<MediaItem> resultList = new ArrayList<MediaItem>();
		Cursor cursor = null;
		try {
			cursor = androidContext.getContentResolver().query(locationUri, projection, selection,
					selectionArgs, sortOrder);
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return resultList;
		}

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
		sc.altitude = 0D;
		mv.geolocation = sc;
		mv.album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
		mv.artists = new String[] { cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)) };
		mv.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
		// mv.width = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.r)) RESOLUTION has result formatted as XxY
		mv.width = cursor.getLong(cursor.getColumnIndex("width"));
		mv.height = cursor.getLong(cursor.getColumnIndex("height"));
		mv.playedTime = (long) cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BOOKMARK));
		// mv.playCount = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.));

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
		ma.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
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
		sc.altitude = 0D;
		mi.geolocation = sc;
		mi.width = (int) cursor.getLong(cursor.getColumnIndex("width"));
		mi.height = (int) cursor.getLong(cursor.getColumnIndex("height"));

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
