package com.campmobile.android.sampleapp.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Band;
import com.campmobile.android.bandsdk.entity.Bands;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class BandListActivity extends BaseToolbarActivity {

	private BandManager bandManager;

	private BandListAdapter adapter;
	private ListView bandListView;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private List<Band> bandList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_band_list);

		initParam();
		initUI();
		setToolbar(R.string.title_band_list);

		listBands();
	}

	private void listBands() {
		bandManager.getBands(new ApiCallbacks<Bands>() {
			@Override
			public void onResponse(Bands bands) {
				bandList.addAll(bands.getBandList());
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();

		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> mCache = new LruCache<>(100);

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				mCache.put(url, bitmap);
			}

			@Override
			public Bitmap getBitmap(String url) {
				return mCache.get(url);
			}
		});
	}

	private void initUI() {
		adapter = new BandListAdapter();
		bandListView = (ListView) findViewById(R.id.band_list);
		bandListView.setAdapter(adapter);
	}

	class BandListAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public BandListAdapter() {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return bandList.size();
		}

		@Override
		public Band getItem(int index) {
			return bandList.get(index);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_band_list_item, parent, false);
			}

			final Band band = getItem(position);

			NetworkImageView coverImageView = (NetworkImageView) convertView.findViewById(R.id.band_cover_img_view);
			coverImageView.setImageUrl(band.getCoverImageUrl(), mImageLoader);

			TextView bandKeyTextView = (TextView) convertView.findViewById(R.id.band_key_text_view);
			bandKeyTextView.setText("band_key : " + band.getBandKey());

			TextView bandNameTextView = (TextView) convertView.findViewById(R.id.band_name_text_view);
			bandNameTextView.setText("band_name : " + band.getName());

			TextView isGuildBandTextView = (TextView) convertView.findViewById(R.id.is_guild_band_text_view);
			isGuildBandTextView.setText("is_guild_band : " + band.isGuildBand());

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(SampleConstants.ParameterKey.BAND, band);
					setResult(RESULT_OK, intent);
					finish();
				}
			});

			return convertView;
		}
	}
}
