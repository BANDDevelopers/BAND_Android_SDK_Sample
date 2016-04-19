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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Band;
import com.campmobile.android.bandsdk.entity.PostInfo;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;
import com.campmobile.band.annotations.api.Page;
import com.campmobile.band.annotations.api.Pageable;

public class BandPostsActivity extends BaseToolbarActivity {
	private BandManager bandManager = null;
	private PostsAdapter postsAdapter;

	private Page page;
	private int bandType;
	private int postType;
	private String bandKey;

	private ListView postListView;

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_posts);

		initParam();
		initUI();
		setToolbarTitle();
		setData();

		if (bandKey != null && !bandKey.isEmpty()) {
			search();
		} else {
			selectBand();
		}
	}

	private void initParam() {
		Intent intent = getIntent();
		bandType = intent.getIntExtra(SampleConstants.BandType.INTENT_EXTRA_KEY, SampleConstants.BandType.GUILD); // Guild, Official
		postType = intent.getIntExtra(SampleConstants.PostType.INTENT_EXTRA_KEY, SampleConstants.PostType.POST); // Post, Notice
		bandKey = intent.getStringExtra(SampleConstants.ParameterKey.BAND_KEY);

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

		bandManager = BandManagerFactory.getSingleton();
		page = Page.FIRST_PAGE;
	}

	private void initUI() {
		postListView = (ListView) findViewById(R.id.post_list);
	}

	private void setToolbarTitle() {
		switch (postType) {
			case SampleConstants.PostType.POST:
				setToolbar(R.string.title_get_post);
				break;
			case SampleConstants.PostType.NOTICE:
				setToolbar(R.string.title_get_notice);
				break;
		}
	}

	private void setData() {
		postsAdapter = new PostsAdapter(this);
		postListView.setAdapter(postsAdapter);
	}

	private void search() {
		if (page == null) {
			return;
		}

		switch (bandType) {
			case SampleConstants.BandType.GUILD:
				if (postType == SampleConstants.PostType.POST) {
					bandManager.getGuildBandPosts(bandKey, page, apiCallBacks);
				} else if (postType == SampleConstants.PostType.NOTICE) {
					bandManager.getGuildBandNotices(bandKey, page, apiCallBacks);
				}
				break;
			case SampleConstants.BandType.OFFICIAL:
				if (postType == SampleConstants.PostType.POST) {
					bandManager.getOfficialBandPosts(bandKey, page, apiCallBacks);
				} else if (postType == SampleConstants.PostType.NOTICE) {
					bandManager.getOfficialBandNotices(bandKey, page, apiCallBacks);
				}
				break;
		}
	}

	private ApiCallbacks<Pageable<PostInfo>> apiCallBacks = new ApiCallbacks<Pageable<PostInfo>>() {
		@Override
		public void onPreExecute() {
			if (postsAdapter != null) {
				postsAdapter.setLoading(true);
			}
		}

		@Override
		public void onResponse(Pageable<PostInfo> response) {
			if (postsAdapter != null) {
				postsAdapter.addPostList(response.getItems());
				postsAdapter.setLoading(false);

				if (response.hasNextPage()) {
					page = response.getNextPage();
				} else {
					page = null;
				}
			}
		}

		@Override
		public void onError(VolleyError error) {
			super.onError(error);
			showMessage(error.getMessage());
		}
	};

	class PostsAdapter extends BaseAdapter {
		Context context;
		List<PostInfo> postList;
		LayoutInflater inflater;
		boolean isLoading;

		public PostsAdapter(Context context) {
			this.postList = new ArrayList<>();
			this.context = context;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.isLoading = false;
		}

		public void addPostList(List<PostInfo> postList) {
			this.postList.addAll(postList);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return postList.size();
		}

		@Override
		public Object getItem(int index) {
			return postList.get(index);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_post_item, parent, false);
			}

			if (!isLoading && position == getCount() - 1 && page != null) {
				search();
			}

			NetworkImageView postImageView = (NetworkImageView) convertView.findViewById(R.id.post_img_view);
			TextView contentTextView = (TextView) convertView.findViewById(R.id.content_text);
			TextView authorTextView = (TextView) convertView.findViewById(R.id.author_text);
			TextView postKeyTextView = (TextView) convertView.findViewById(R.id.post_key_text);

			PostInfo postInfo = postList.get(position);

			if (postInfo.getPhotos().size() > 0) {
				postImageView.setImageUrl(postInfo.getPhotos().get(0).getUrl(), mImageLoader);
			} else {
				postImageView.setImageUrl(null, mImageLoader);
			}

			contentTextView.setText("content : " + postInfo.getContent());
			authorTextView.setText("author name : " + postInfo.getAuthor().getName());
			postKeyTextView.setText("post_key : " + postInfo.getPostKey());

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String postKey = postList.get(position).getPostKey();
					gotoBandPostDetail(postKey);
				}
			});

			return convertView;
		}

		public void setLoading(boolean isLoading) {
			this.isLoading = isLoading;
		}
	}

	private void gotoBandPostDetail(String postKey) {
		bandManager.openPost(this, bandKey, postKey);
	}

	private void selectBand() {
		Intent intent = new Intent(this, BandListActivity.class);
		startActivityForResult(intent, SampleConstants.RequestCode.BAND_SELECT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case BAND_SELECT:
				if (resultCode == RESULT_OK) {
					Band band = data.getParcelableExtra(SampleConstants.ParameterKey.BAND);
					if (band != null) {
						bandKey = band.getBandKey();
						search();
						break;
					}
				} else {
					finish();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}