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
import com.campmobile.android.bandsdk.entity.Friend;
import com.campmobile.android.bandsdk.entity.Friends;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class FriendListActivity extends BaseToolbarActivity {

	private BandManager bandManager;

	private FriendListAdapter adapter;
	private ListView friendListView;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private List<Friend> friendList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);

		initParam();
		initUI();
		setToolbar(R.string.title_friend_list);

		listFriends();
	}

	private void listFriends() {
		bandManager.getFriends(new ApiCallbacks<Friends>() {
			@Override
			public void onResponse(Friends friends) {
				friendList.addAll(friends.getFriendList());
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
		adapter = new FriendListAdapter();
		friendListView = (ListView) findViewById(R.id.friend_list);
		friendListView.setAdapter(adapter);
	}

	class FriendListAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public FriendListAdapter() {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return friendList.size();
		}

		@Override
		public Friend getItem(int index) {
			return friendList.get(index);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_friend_list_item, parent, false);
			}

			final Friend friend = getItem(position);

			NetworkImageView coverImageView = (NetworkImageView) convertView.findViewById(R.id.friend_img_view);
			coverImageView.setImageUrl(friend.getProfileImageUrl(), mImageLoader);
			coverImageView.setDefaultImageResId(R.drawable.ico_pf_default);
			
			TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
			nameTextView.setText("name : " + friend.getName());

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(SampleConstants.ParameterKey.FRIEND, friend);
					setResult(RESULT_OK, intent);
					finish();
				}
			});

			return convertView;
		}
	}
}