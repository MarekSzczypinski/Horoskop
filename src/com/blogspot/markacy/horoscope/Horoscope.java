package com.blogspot.markacy.horoscope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

public class Horoscope {
	private static ArrayList<HashMap<String, Object>> horoscope;
	private String json;
	
	private final String KEY_SIGN = "signId";
	private final String KEY_DATE = "dueDate";
	private final String KEY_HOROSCOPE = "horoscope";

	private Horoscope() {
		horoscope = new java.util.ArrayList<HashMap<String,Object>>();
	}

	/**
	 * HoroscopeHolder is loaded on the first execution of
	 * Horoscope.getInstance() or the first access to HoroscopeHolder.INSTANCE,
	 * not before.
	 */
	private static class HoroscopeHolder {
		private static final Horoscope INSTANCE = new Horoscope();
	}

	public static Horoscope getInstance() {
		return HoroscopeHolder.INSTANCE;
	}
	
	@SuppressLint("NewApi") // for some reason Lint treated string in Locale() as pattern for SimpleDateFormat
	public synchronized HashMap<String, Object> getHoroscopeForDateAndSign(Date date,
			long sign) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pl", "pl_PL"));
		int horoscopeLength = horoscope.size();
		HashMap<String, Object> tmpHashMap = new HashMap<String, Object>();
		for (int i = 0; i < horoscopeLength; i++) {
			tmpHashMap = horoscope.get(i);
			if ((Integer)tmpHashMap.get(KEY_SIGN) == sign) {
				if ( ((String)tmpHashMap.get(KEY_DATE)).equals(sdf.format(date)) ) {
					return tmpHashMap;
				}
			}
		}
		return null;
	}
	
	// Either return HashMap, or only String with appropriate horoscope. TBD
	public synchronized HashMap<String, Object> getHoroscopeForSign(long sign) {
		Date now = new Date();
		return getHoroscopeForDateAndSign(now, sign);
	}

	public synchronized void refreshHoroscope() {
		horoscope.clear();
		downloadHoroscope();
		parseHoroscopeFromJson();
	}

	private void downloadHoroscope() {
		final String url = "http://horoskop.wp.pl/app/horoscopes?ids=88";
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer();
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			json = sb.toString();
			// System.out.println(page);
		} catch (URISyntaxException use) {
			// not ever gonna happen
		} catch (IOException ioe) {
			Log.d("getPage - ioe", ioe.getMessage());
			json = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void parseHoroscopeFromJson() {
		try {
			JSONArray jsonHoroscope = new JSONArray(json);
			int length = jsonHoroscope.length();
			HashMap<String, Object> tmpHashMap;
			for (int i = 0; i < length; i++) {
				tmpHashMap = new HashMap<String, Object>();
				JSONObject singleSign = jsonHoroscope.getJSONObject(i);
				tmpHashMap.put(KEY_SIGN, singleSign.getInt(KEY_SIGN));
				tmpHashMap.put(KEY_DATE, singleSign.getString(KEY_DATE));
				String horoscopeString = singleSign.getString(KEY_HOROSCOPE);
				int index = horoscopeString.indexOf("<br");
				if (index > 0) {
					tmpHashMap.put(KEY_HOROSCOPE, horoscopeString.substring(0, index));
				} else {
					tmpHashMap.put(KEY_HOROSCOPE, singleSign.get(KEY_HOROSCOPE));
				}
				horoscope.add(tmpHashMap);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
