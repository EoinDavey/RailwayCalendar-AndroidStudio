package com.powerblock.railwaycalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class CustomWebView extends WebView {
	
	public CustomWebView(Context context) {
		super(context);
		activatePreferences();
	}
	
	public CustomWebView(Context context, AttributeSet attrs){
		super(context, attrs);
		activatePreferences();
	}
	
	public CustomWebView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		activatePreferences();
	}
	
	public void activatePreferences(){
		getSettings().setBuiltInZoomControls(true);
		getSettings().setSupportZoom(true);
	}

}
