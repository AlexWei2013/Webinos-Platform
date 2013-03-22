package org.webinos.impl.mediacontent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.webinos.api.DeviceAPIError;
import org.webinos.api.ErrorCallback;
import org.webinos.api.mediacontent.MediaFolder;
import org.webinos.api.mediacontent.MediaFolderArraySuccessCallback;
import org.webinos.api.mediacontent.MediaFolderStorageType;
import org.webinos.api.mediacontent.PendingGetOperation;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class PendingGetOperationImpl extends PendingGetOperation {

	private static final String TAG = "org.webinos.impl.MediaContent.MediaSourceImpl.PendingGetOperation";
	private HashMap<String, String> _folderIDtoPathMapping = null;
	MediaFoldersAggregator _mfa;
	
	private MediaFolderArraySuccessCallback _mfasCallback;
	private ErrorCallback _errCallback;
	
	@Override
	public void cancel() {
		if(_mfa!=null)
		_mfa.cancel(true);

	}
	
	public PendingGetOperation Get(MediaFolderArraySuccessCallback mfasCB, ErrorCallback errCB, HashMap<String, String> folderIDtoPathMapping){
		this._mfasCallback = mfasCB;
		this._errCallback = errCB;
		this._folderIDtoPathMapping = folderIDtoPathMapping;
		
		_mfa = new MediaFoldersAggregator();
		_mfa.execute(null);
		
		
		return this;
	}
	
	private class MediaFoldersAggregator extends AsyncTask<Void, Void, MediaFolder[]> {

		@Override
		protected MediaFolder[] doInBackground(Void... params) {
			if(this.isCancelled())
				return null;
			
			try {
				ArrayList<File> mediaFolders = new ArrayList<File>();
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
				mediaFolders.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
				
				if(this.isCancelled())
					return null;
				
				MediaFolder[] resultArray = new MediaFolder[mediaFolders.size()];
				for (int i = 0; i < mediaFolders.size(); i++) {
					try {
						resultArray[i] = mediaFolderFromFile(mediaFolders.get(i));
					} catch (Exception e) {
						_errCallback.onerror(new DeviceAPIError(DeviceAPIError.SECURITY_ERR));
						return null;
					}
					
					if(this.isCancelled())
						return null;
				}
				return resultArray;
			} catch (Exception e) {
				_errCallback.onerror(new DeviceAPIError(DeviceAPIError.NOT_SUPPORTED_ERR));
				Log.i(TAG, "Failed while getting media folders with following error:" + e.toString());
				return null;
			}
		}

		@Override
		protected void onPostExecute(MediaFolder[] result) {
			
			if (result != null && !(this.isCancelled()))
				_mfasCallback.onsuccess(result);

//			super.onPostExecute(result);
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
			// TODO determine is on internal or external storage, but media is usually on external storage. If no microSD card is supported,
			// Android still treats media files as being on external storage.
			mf.storageType = MediaFolderStorageType.EXTERNAL.toString();
			// Careful
			String d = new String().valueOf(file.lastModified());
			long newFileDateMillis = (Long.parseLong(d) / 1000) * 1000;
			mf.modifiedDate = new Date(newFileDateMillis);

			// Add the folder ID and path to the mapping. This is used in searches when a folder ID is provided.
			if (_folderIDtoPathMapping != null)
				_folderIDtoPathMapping.put(mf.id, mf.folderURI);

			Log.i(TAG, "MediaFolder: " + mf.title + " @:" + mf.folderURI + " ID: " + mf.id + " on:"
					+ mf.modifiedDate.toLocaleString() + " Type: " + mf.storageType);
			return mf;
		}
	}
}
