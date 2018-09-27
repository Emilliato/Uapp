package net.simpledev.leo.uapp;

/**
 * Created by LEoe on 8/15/2018.
 */

public class MyUrl {
	private String urlName;
	private String urlString;
	private String urlId;
	
	public MyUrl() {
	}
	
	public MyUrl(String urlId, String urlName, String urlString ) {
		this.urlName = urlName;
		this.urlString = urlString;
		this.urlId = urlId;
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public String getUrlString() {
		return urlString;
	}
	
	public String getUrlId() {
		return urlId;
	}
	
	@Override
	public String toString() {
		return getUrlName();
	}
}
