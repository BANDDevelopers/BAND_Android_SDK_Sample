package com.campmobile.android.sampleapp;

import android.app.Application;

import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;

public class SampleApplication extends Application {
	public static final String CLIENT_ID = "changed with your client_id";
	public static final String CLIENT_SECRET = "changed with your secret_id";

	@Override
	public void onCreate() {
		super.onCreate();
		BandManager bandManager = BandManagerFactory.getSingleton();
		bandManager.init(this, SampleApplication.CLIENT_ID, SampleApplication.CLIENT_SECRET, false);
	}
}
