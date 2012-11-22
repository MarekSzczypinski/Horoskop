package com.blogspot.markacy.horoscope;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.blogspot.markacy.horoscope.SignListFragment.OnSignSelectedListener;

public class MainActivity extends FragmentActivity implements
		OnSignSelectedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signlist_fragment);
		
		Intent startHoroscopeDownload = new Intent(this, HoroscopeDownloadService.class);
		startService(startHoroscopeDownload);
	}

	public void onSignSelected(long id) {
		DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_fragment);
		if (detailsFragment == null || !detailsFragment.isInLayout()) {
			Intent i = new Intent(this, DetailedActivity.class);
			i.putExtra("signId", id);
			startActivity(i);
		} else {
			detailsFragment.updateFields(id);
		}
	}
	
}
