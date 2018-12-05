package com.welcare.hrvemotionmanage.hrvinterface;

public abstract class RequestCallback {
	
	public abstract void onSuccess(String result);
	public abstract void onError(String error);
	public void onProgress(int progress){}
	public void onCode(int code){}

}
