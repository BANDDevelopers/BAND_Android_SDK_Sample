package com.campmobile.android.sampleapp.activity;

import static com.campmobile.android.sampleapp.SampleConstants.RequestCode.BAND_SELECT;

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
import com.campmobile.android.bandsdk.entity.Member;
import com.campmobile.android.bandsdk.entity.Members;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class MemberListActivity extends BaseToolbarActivity {

	private BandManager bandManager;
	private Band selectedBand;

	private BandMemberListAdapter adapter;
	private ListView memberListView;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private List<Member> bandMemberList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_list);

		initParam();
		initUI();
		setToolbar(R.string.title_member_list);

		Intent intent = new Intent(this, BandListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.BAND_SELECT);
	}

	private void listBandMembers() {
		bandManager.getBandMembers(selectedBand.getBandKey(), true, new ApiCallbacks<Members>() {
			@Override
			public void onResponse(Members members) {
				bandMemberList.addAll(members.getMemberList());
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
		adapter = new BandMemberListAdapter();
		memberListView = (ListView) findViewById(R.id.member_list);
		memberListView.setAdapter(adapter);
	}

	class BandMemberListAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public BandMemberListAdapter() {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return bandMemberList.size();
		}

		@Override
		public Member getItem(int index) {
			return bandMemberList.get(index);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_member_list_item, parent, false);
			}

			final Member member = getItem(position);

			NetworkImageView coverImageView = (NetworkImageView) convertView.findViewById(R.id.member_img_view);
			coverImageView.setImageUrl(member.getProfileImageUrl(), mImageLoader);
			coverImageView.setDefaultImageResId(R.drawable.ico_pf_default);

			TextView userKeyTextView = (TextView) convertView.findViewById(R.id.user_key_text_view);
			userKeyTextView.setText("user_key : " + member.getUserKey());

			TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
			nameTextView.setText("name : " + member.getName());

			TextView flagTextView = (TextView) convertView.findViewById(R.id.flag_text_view);
			flagTextView.setText("app_member : " + member.isAppMember() + ", message_allowed : " + member.isMessageAllowed());

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(SampleConstants.ParameterKey.MEMBER, member);
					intent.putExtra(SampleConstants.ParameterKey.BAND, selectedBand);
					setResult(RESULT_OK, intent);
					finish();
				}
			});

			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case BAND_SELECT:
				if (resultCode == RESULT_OK) {
					selectedBand = data.getParcelableExtra(SampleConstants.ParameterKey.BAND);
					listBandMembers();
				} else {
					finish();
				}
				break;
		}
	}
}