package com.blogspot.markacy.horoscope;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.markacy.lazylist.ImageLoader;

public class LazyAdapter extends BaseAdapter {
	
	private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private String signs[];
	

	public LazyAdapter(Activity activity, int imagesUrlsResource) {
		this.activity = activity;
		inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(this.activity);
		Resources resources = this.activity.getResources();
		signs = resources.getStringArray(R.array.signs);
		data = resources.getStringArray(imagesUrlsResource);
	}

	public int getCount() {
		return 12;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 View view = convertView;
	        if(convertView == null) {
	            view = inflater.inflate(R.layout.signs_row, null);
	        }
	        TextView text=(TextView)view.findViewById(R.id.row_text);;
	        ImageView image=(ImageView)view.findViewById(R.id.image);
	        text.setText(signs[position]);
	        imageLoader.DisplayImage(data[position], image);
	        return view;
	}

}
