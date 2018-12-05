package com.welcare.hrvemotionmanage.entity;

import org.apache.http.NameValuePair;

public class HRVParameter implements NameValuePair {
	String name;
	String value;
	
	public HRVParameter() {
	}
	public HRVParameter(String name,String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
