package com.welcare.hrvemotionmanage.hrvinterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.welcare.hrvemotionmanage.entity.HRVParameter;

import android.content.Context;

public class HRVEmotionUtile {
	private Context context;
	
	private static HRVEmotionUtile instance;
	private static String sync = "sync";
	
	private HRVEmotionUtile() {
		
	}
	public static HRVEmotionUtile getInstance() {
		if(instance == null)
		{
			synchronized (sync) {
				if(instance == null)
				{
					instance = new HRVEmotionUtile();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 初始化
	 * **/
	public void init(Context context) {
		
		this.context = context;
	}
	
	/**
	 * 获取情绪分析数据
	 * **/
	public void getHRVEmotion(String deviceId,RequestCallback requestCallback)
	{
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new HRVParameter("deviceid",deviceId));
		new HTTPAsyncTask(requestCallback).execute(HTTPInterface.GET_HRV_EMOTION,nvps);
		
	}
	/**
	 * 获取情绪分析数据
	 *
	 * @param deviceId
	 * @param requestCallback
	 * @param file**/
	public void getHRVEmotion(String deviceId,RequestCallback requestCallback,File file)
	{
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new HRVParameter("deviceid",deviceId));
		new HTTPAsyncTask(requestCallback).execute(HTTPInterface.GET_HRV_EMOTION,nvps,file);
		
	}
	/**
	 * 获取情绪分析数据
	 * **/
	public void getHRVEmotion(String deviceId, final RequestCallback requestCallback, byte[] datas)
	{
		if(datas.length<6000)
		{
			if(requestCallback != null) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("error","Data anomaly");
//					jsonObject.put("errorCode","-1001");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				requestCallback.onSuccess(jsonObject.toString());
			}
			return;
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new HRVParameter("deviceid",deviceId));
		new HTTPAsyncTask(new RequestCallback() {
			@Override
			public void onSuccess(String result) {
				if(requestCallback != null)
				{
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("pressureScale","Normal");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					requestCallback.onSuccess(jsonObject.toString());
				}
			}

			@Override
			public void onError(String error) {
				if(requestCallback != null)
				{
					requestCallback.onError(error);
				}
			}

			@Override
			public void onCode(int code) {
				if(requestCallback != null)
				{
					requestCallback.onCode(code);
				}
			}

			@Override
			public void onProgress(int progress) {
				super.onProgress(progress);
				if(requestCallback != null)
				{
					requestCallback.onProgress(progress);
				}
			}
		}).execute(HTTPInterface.GET_HRV_EMOTION,nvps,datas);
		
	}
		
}
