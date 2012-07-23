package com.odistagon.glone;

import android.app.Application;
import android.content.Context;

public class GloneApp extends Application
{
	private static GlOneDoc	m_doc;
	private static Context	mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		// document have to be instantiated before views
		m_doc = new GlOneDoc();
	}

	public static Context getContext(){
		return	mContext;
	}

	/** provides fast access path to document object for views, etc.
	 */
	public static GlOneDoc getDoc() {
		return	m_doc;
	}

}
