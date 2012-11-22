package com.blogspot.markacy.horoscope;

import java.util.Date;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.markacy.lazylist.ImageLoader;

public class DetailsFragment extends Fragment {

	public static final String MESSAGE = "com.blogspot.markacy.horoscope.DONE";
	private HoroscopeReceiver receiver;
	private static final String TAG = "Horoscope";
	private ImageView imageView;
	private TextView horoscopeView;
	private Button refreshButton;
	private Long mSignId;
	private ProgressDialog refreshProgressDlg;
	private ImageLoader lazyImageLoader;
	private Resources resources;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.detailed, container, false);

		mSignId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState.getSerializable("signId");
		if (mSignId == null) {
			Bundle extras = getActivity().getIntent().getExtras();
			mSignId = extras != null ? extras.getLong("signId") : null;
		}

		refreshProgressDlg = new ProgressDialog(getActivity()); 
		refreshProgressDlg.setTitle("Czekaj chwilę..."); 
		refreshProgressDlg.setMessage("Wróżka Krystyna właśnie przesyła horoskop..."); 
		refreshProgressDlg.setIndeterminate(true);
		
		imageView = (ImageView) view.findViewById(R.id.detail_image);
		horoscopeView = (TextView) view.findViewById(R.id.horoscope_txt);
		horoscopeView.setMovementMethod(new ScrollingMovementMethod());
		refreshButton = (Button) view.findViewById(R.id.refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent startHoroscopeDownload = new Intent(getActivity(),
						HoroscopeDownloadService.class);
				getActivity().startService(startHoroscopeDownload);
				refreshProgressDlg.show();
			}
		});
		
		lazyImageLoader = new ImageLoader(getActivity());
		resources = getActivity().getResources();		
		
		receiver = new HoroscopeReceiver();
		IntentFilter broadcastFilter = new IntentFilter(MESSAGE);
		getActivity().registerReceiver(receiver, broadcastFilter);
		return view;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void updateFields(long rowId) {
		mSignId = rowId;
		populateFields();
	}

	private void populateFields() {
		if (mSignId != null) {
			HashMap<String, Object> tmpHashMap = Horoscope.getInstance()
					.getHoroscopeForSign(mSignId);
			// workaround - on Sunday they issue new horoscope set, starting
			// next day...
			if (tmpHashMap == null) {
				Date date = new Date();
				date.setDate(date.getDate() + 1);
				tmpHashMap = Horoscope.getInstance()
						.getHoroscopeForDateAndSign(date, mSignId);
			}
			String text = (String) tmpHashMap.get("horoscope");
			horoscopeView.setText(text);
			
			String []data = resources.getStringArray(R.array.sign_image_urls);
			lazyImageLoader.DisplayImage(data[(int)(mSignId - 1)], imageView);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("signId", mSignId);
	}

	@Override
	public void onResume() {
		super.onResume();
		populateFields();
	}

	private class HoroscopeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "got broadcast from service..");
			populateFields();
			if (refreshProgressDlg.isShowing()) {
				refreshProgressDlg.dismiss();
			}

		}

	}

}
