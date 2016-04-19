package com.campmobile.android.sampleapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.LoginCallbacks;
import com.campmobile.android.bandsdk.entity.AccessToken;
import com.campmobile.android.sampleapp.R;

public class LoginActivity extends Activity {
	private BandManager bandManager;
	private ProgressDialog mProgressDialog;

	private ImageButton loginImageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bandManager = BandManagerFactory.getSingleton();

		setContentView(R.layout.activity_login);
		loginImageButton = (ImageButton) findViewById(R.id.btn_band_login);
		loginImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bandManager.login(LoginActivity.this, onLoginApiCallbacks);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		bandManager.onActivityResult(this, requestCode, resultCode, data, onLoginApiCallbacks);
	}

	LoginCallbacks<AccessToken> onLoginApiCallbacks = new LoginCallbacks<AccessToken>() {
		@Override
		public void onPreExecute() {
			mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "Login process loading", true);
		}

		@Override
		public void onResponse(AccessToken response) {
			Toast.makeText(LoginActivity.this, "Login succeeded.", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onError(VolleyError volleyError) {
			Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(LoginActivity.this, "Login canceled.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onPostExecute(boolean isSuccess) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}
	};
}
