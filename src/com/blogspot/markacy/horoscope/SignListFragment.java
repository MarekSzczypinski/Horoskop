package com.blogspot.markacy.horoscope;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class SignListFragment extends ListFragment {
	OnSignSelectedListener signSelectionListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillData();
	}

	private void fillData() {
		LazyAdapter lazyAdapter = new LazyAdapter(getActivity(), R.array.sign_image_urls);
		setListAdapter(lazyAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		signSelectionListener.onSignSelected(++id);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			signSelectionListener = (OnSignSelectedListener) activity;
		} catch (ClassCastException exception) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSignSelectedListener!");
		}
	}

	public interface OnSignSelectedListener {
		public void onSignSelected(long id);
	}

}
