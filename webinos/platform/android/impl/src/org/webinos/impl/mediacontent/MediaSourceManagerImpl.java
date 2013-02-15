package org.webinos.impl.mediacontent;

import org.meshpoint.anode.module.IModule;
import org.meshpoint.anode.module.IModuleContext;
import org.webinos.api.mediacontent.MediaSource;
import org.webinos.api.mediacontent.MediaSourceManager;

public class MediaSourceManagerImpl extends MediaSourceManager implements IModule {

	private MediaSource _mediaSource;

	
	/*
	 * IModule methods
	 * @see org.meshpoint.anode.module.IModule#startModule(org.meshpoint.anode.module.IModuleContext)
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

	
	/*
	 * MediaSourceManager methods
	 */
	@Override
	public MediaSource getLocalMediaSource() {
		
		if(this._mediaSource==null){
			this._mediaSource = new MediaSourceImpl();
		}
		
		return this._mediaSource;
	}

}
