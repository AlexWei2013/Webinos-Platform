package org.webinos.api.mediacontent;

public enum MediaFolderStorageType {
	INTERNAL(0), 
	EXTERNAL(1),
	UNKNOWN(2);
	
	private Integer integerValue;
	private MediaFolderStorageType(Integer integerValue) {
		 this.integerValue = integerValue;
	}
	
	public Integer displayintegerValue(){ return this.integerValue;}
	
	@Override
	public String toString(){
		return this.integerValue.toString();
	}
}
