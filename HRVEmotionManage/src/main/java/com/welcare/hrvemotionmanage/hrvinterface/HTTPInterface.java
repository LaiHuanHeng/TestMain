package com.welcare.hrvemotionmanage.hrvinterface;

public class HTTPInterface {
	
	public static String serviceTestIP = "http://yg.welcare-tech.com.cn:8080/";
	public static String serviceIP = "http://hi-watch.com.cn/";
	private static boolean isTest = true;
	private static String serviceURL = isTest?serviceTestIP:serviceIP;
	public static void setTest(boolean isTest) {
		HTTPInterface.isTest = isTest;
		serviceURL = HTTPInterface.isTest?serviceTestIP:serviceIP;
		
		GET_HRV_EMOTION = serviceURL+"hiwatchclient/gethrv.htm";
		
	}
	//获取HRV情绪管理
	public static  String GET_HRV_EMOTION = serviceURL+"hiwatchclient/gethrv.htm";
	


}
