package org.webinos.impl.mediacontent;

import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.webinos.api.DeviceAPIError;
import org.webinos.api.ErrorCallback;
import org.webinos.api.mediacontent.AbstractFilter;
import org.webinos.api.mediacontent.AttributeFilter;
import org.webinos.api.mediacontent.AttributeRangeFilter;
import org.webinos.api.mediacontent.CompositeFilter;
import org.webinos.api.mediacontent.CompositeFilterType;
import org.webinos.api.mediacontent.FilterMatchFlag;
import org.webinos.api.mediacontent.MediaAudio;
import org.webinos.api.mediacontent.MediaImage;
import org.webinos.api.mediacontent.MediaImageOrientation;
import org.webinos.api.mediacontent.MediaItem;
import org.webinos.api.mediacontent.MediaItemArraySuccessCallback;
import org.webinos.api.mediacontent.MediaItemType;
import org.webinos.api.mediacontent.MediaVideo;
import org.webinos.api.mediacontent.PendingFindOperation;
import org.webinos.api.mediacontent.SimpleCoordinates;
import org.webinos.api.mediacontent.SortMode;
import org.webinos.api.mediacontent.SortModeOrder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

public class PendingFindOperationImpl extends PendingFindOperation {

	private MediaItemArraySuccessCallback _miasCB;
	private ErrorCallback _errCB;

	private static final String TAG = "org.webinos.impl.MediaContent.MediaSourceImpl.PendingFindOperation";
	private Context _androidContext;
	private HashMap<String, String> filterMatchFlagToSqlOperatorMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 854094582574467265L;

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
		private static final long serialVersionUID = -8804452819301879788L;

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
			put("mimeType", MediaStore.Images.Media.MIME_TYPE);
		}
	};

	private HashMap<String, String> attrNameToVideoColumnNameMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6927426327099963360L;

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
			put("mimeType", MediaStore.Video.Media.MIME_TYPE);
		}
	};

	private HashMap<String, String> attrNameToAudioColumnNameMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5361469383607061630L;

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
			put("mimeType", MediaStore.Audio.Media.MIME_TYPE);
		}
	};

	private HashMap<String, String> folderIDtoPathMapping = null;

	/*
	 * Some vars for the current search.
	 */
	String _folderID;
	AbstractFilter _abstractFilter;
	SortMode _sortMode;
	Integer _resultCount;
	Integer _resultOffset;

	private ItemsFinderAsync ifAsync = null;

	private int artificialResultLimit = 10;

	@Override
	public void cancel() {
		if (this.ifAsync != null)
			ifAsync.cancel(true);
	}

	public PendingFindOperationImpl Get(MediaItemArraySuccessCallback miasCB, ErrorCallback errorCallback,
			Context androidContext, HashMap<String, String> folderIDtoPathMapping, String folderId,
			AbstractFilter abstractFilter, SortMode sortMode, Integer count, Integer offset) {

		this._miasCB = miasCB;
		this._errCB = errorCallback;
		this._androidContext = androidContext;
		this.folderIDtoPathMapping = folderIDtoPathMapping;
		this._folderID = folderId;
		this._abstractFilter = abstractFilter;
		this._sortMode = sortMode;
		this._resultCount = count;
		this._resultOffset = offset;

		this.artificialResultLimit = 20;

		this.ifAsync = new ItemsFinderAsync();
		ifAsync.execute(null);

		return this;
	}

	private class ItemsFinderAsync extends AsyncTask<Void, Void, MediaItem[]> {

		@Override
		protected MediaItem[] doInBackground(Void... paramArrayOfParams) {

			if (this.isCancelled())
				return null;

			MediaItem[] result = null;
			try {
				ArrayList<MediaItem> mediaItemsList = new ArrayList<MediaItem>();

				ArrayList<MediaItem> imageQueryArrayList = queryMediaFolder(MediaItemType.IMAGE, _folderID,
						_abstractFilter, _sortMode, _resultCount, _resultOffset);
				mediaItemsList.addAll(imageQueryArrayList);
				if (this.isCancelled())
					return null;

				ArrayList<MediaItem> videoQueryArrayList = queryMediaFolder(MediaItemType.VIDEO, _folderID,
						_abstractFilter, _sortMode, _resultCount, _resultOffset);
				mediaItemsList.addAll(videoQueryArrayList);
				if (this.isCancelled())
					return null;

				ArrayList<MediaItem> audioQueryArrayList = queryMediaFolder(MediaItemType.AUDIO, _folderID,
						_abstractFilter, _sortMode, _resultCount, _resultOffset);
				mediaItemsList.addAll(audioQueryArrayList);
				if (this.isCancelled())
					return null;

				mediaItemsList.trimToSize();
				result = new MediaItem[mediaItemsList.size()];
				result = mediaItemsList.toArray(result);
			} catch (Exception e) {
				_errCB.onerror(new DeviceAPIError(DeviceAPIError.TYPE_MISMATCH_ERR));
				return null;
			}
			if (result == null) {
				_errCB.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
				return null;
			}

			return result;

		}

		@Override
		protected void onPostExecute(MediaItem[] result) {

			if (result == null) {
				_errCB.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
				return;
			}

			_miasCB.onsuccess(result);
		}

		/*
		 * Some private methods
		 */
		private String getSelectionFromFilter(MediaItemType mit, AbstractFilter filter, String folderID)
				throws InvalidClassException {
			if (filter == null && (folderID == null || folderID.isEmpty()))
				return null;

			String locationSelection = getSelectionFromFolderID(mit, folderID);
			String attributesSelection = null;
			if (filter != null) {
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
			if (filter == null)
				return null;

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
			selectionArgs.trimToSize();
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
			if (sortMode == null && count == null && offset == null) {
				return null;
			}
			StringBuilder sb = new StringBuilder();

			// If sortMode is missing, count will fail due to Android's query method so I'm providing a default sortMode.
			SortMode safeSortMode;
			if (sortMode == null) {
				safeSortMode = new SortMode();
				safeSortMode.attributeName = "size";
				safeSortMode.order = SortModeOrder.DESC.toString();
			} else {
				safeSortMode = sortMode;
			}

				// Determine the column name to sort by
				String colName = null;
				switch (mit) {
				case IMAGE: {
					colName = attrNameToImageColumnNameMapping.get(safeSortMode.attributeName);
					break;
				}
				case AUDIO: {
					colName = attrNameToAudioColumnNameMapping.get(safeSortMode.attributeName);
					break;
				}
				case VIDEO: {
					colName = attrNameToVideoColumnNameMapping.get(safeSortMode.attributeName);
					break;
				}
				default:
					return null;
				}
				if (colName != null && !colName.isEmpty()) {
					sb.append(" " + colName);
				}

				// Sort ascending or descending
				String ascOrDesc = safeSortMode.order.toUpperCase().equals("DESC") ? " DESC" : " ASC";
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
				concretePopulateMediaItemMethod = ItemsFinderAsync.class.getDeclaredMethod(
						"populateMediaAudioFromCursor", params);
				break;
			}
			case IMAGE: {
				locationUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				concreteMediaItemClass = MediaImage.class;
				Class[] params = new Class[] { MediaImage.class, Cursor.class };
				concretePopulateMediaItemMethod = ItemsFinderAsync.class.getDeclaredMethod(
						"populateMediaImageFromCursor", params);
				break;
			}
			case VIDEO: {
				locationUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				concreteMediaItemClass = MediaVideo.class;
				Class[] params = new Class[] { MediaVideo.class, Cursor.class };
				concretePopulateMediaItemMethod = ItemsFinderAsync.class.getDeclaredMethod(
						"populateMediaVideoFromCursor", params);
				break;
			}
			default: {
				locationUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				concreteMediaItemClass = MediaImage.class;
				Class[] params = new Class[] { MediaImage.class, Cursor.class };
				concretePopulateMediaItemMethod = ItemsFinderAsync.class.getDeclaredMethod(
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

			// abstractFilter = cf;
			// folderId = "c3418983-f961-37fb-9950-49c67426dc08"; // pics folder

			try {
				selection = getSelectionFromFilter(mit, abstractFilter, folderId);
				if (selection != null && !selection.isEmpty())
					selectionArgs = getSelectionArgsFromFilter(abstractFilter);
			} catch (Exception e) {
				selection = null;
				selectionArgs = null;
			}

			SortMode tempSortMode = new SortMode();
			tempSortMode.attributeName = "size";
			tempSortMode.order = SortModeOrder.DESC.toString();
			sortOrder = getSortModeCountOffsetString(mit, sortMode, count, offset);
			// end For testing

			ArrayList<MediaItem> resultList = new ArrayList<MediaItem>();
			Cursor cursor = null;
			try {
				Log.i(TAG, " ########################################### \n");
				Log.i(TAG, "Location: " + locationUri);
				Log.i(TAG, "Projection: " + projection);
				Log.i(TAG, "Selection: " + selection);
				Log.i(TAG, "Selection args: " + selectionArgs);
				Log.i(TAG, "Sort/ ORDER /Count: " + sortOrder);
				cursor = _androidContext.getContentResolver().query(locationUri, projection, selection,
						selectionArgs, sortOrder);
			} catch (Exception e) {
				e.printStackTrace();
				return resultList;
			}
			try {
				if (!cursor.moveToFirst()) {
					return resultList;
				}
				// Carefull! - in a while( cursor.moveToNext() ) loop you skip the first result.
				do {
					artificialResultLimit--;
					MediaItem mi = (MediaItem) concreteMediaItemClass.newInstance();
					// populateMediaImageFromCursor(mi, cursor);
					concretePopulateMediaItemMethod.invoke(this, mi, cursor);
					resultList.add(mi);
				} while (cursor.moveToNext() && artificialResultLimit > 0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
				cursor = null;
			}

			resultList.trimToSize();
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
			mv.artists = new String[] { cursor
					.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)) };
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
			ma.artists = new String[] { cursor
					.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) };
			ma.composers = new String[] { cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.COMPOSER)) };
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

	}

}
