package com.campmobile.android.sampleapp.activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Profile;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.task.DownloadImageTask;

public class MemberProfileActivity extends BaseToolbarActivity {
	private BandManager bandManager;
	private ImageView profileImgae;
	private TextView userTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bandManager = BandManagerFactory.getSingleton();

		setContentView(R.layout.activity_member_profile);
		setToolbar(R.string.title_member_profile);
		profileImgae = (ImageView) findViewById(R.id.user_profile_img);
		userTextView = (TextView) findViewById(R.id.user_text);

		bandManager.getProfile(new ApiCallbacks<Profile>() {
			@Override
			public void onResponse(Profile profile) {
				String profileImageUrl = profile.getProfileImageUrl();
				if (profileImageUrl != null && profileImageUrl.length() > 0) {
					new DownloadImageTask(profileImgae).execute(profileImageUrl);
				}

				StringBuilder sb = new StringBuilder();
				sb.append("<B><U>Member Name   </B></U> : ").append(profile.getUserName()).append("<BR/>");
				sb.append("<B><U>Is App Memeber</B></U> : ").append(profile.isConnected()).append("<BR/>");
				sb.append("<B><U>Allow Message </B></U> : ").append(profile.isMessageAllowed()).append("<BR/>");
				sb.append("<B><U>User Key      </B></U> : <BR>").append(profile.getUserKey()).append("<BR/>");

				userTextView.setText(Html.fromHtml(sb.toString()));
			}

			@Override
			public void onError(VolleyError error) {
				Log.e("@API", error.getMessage(), error);
				showMessage(error.getMessage());
			}
		});
	}

}
