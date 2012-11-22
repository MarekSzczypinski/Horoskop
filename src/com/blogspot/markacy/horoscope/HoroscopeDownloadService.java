package com.blogspot.markacy.horoscope;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class HoroscopeDownloadService extends IntentService {

	public HoroscopeDownloadService() {
		super("HoroscopeDownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("SERVICE", "Starting downloading...");
		Horoscope.getInstance().refreshHoroscope();
		Log.d("SERVICE", "Finished downloading...");
		Intent messageIntent = new Intent(DetailsFragment.MESSAGE);
		sendBroadcast(messageIntent);
	}

}
