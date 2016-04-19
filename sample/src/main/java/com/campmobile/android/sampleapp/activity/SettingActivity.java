package com.campmobile.android.sampleapp.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.sampleapp.R;

public class SettingActivity extends BaseToolbarActivity {
	private BandManager bandManager = null;

	private Button btnSplash;
	private Button btnKeyHashCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initParam();
		initUI();
		setToolbar(R.string.title_setting);
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();
	}

	private void initUI() {
		btnSplash = (Button) findViewById(R.id.btn_splash);
		btnKeyHashCheck = (Button) findViewById(R.id.btn_key_hash_check);

		btnSplash.setOnClickListener(onClickListener);
		btnKeyHashCheck.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_splash:
					Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
					intent.putExtra("isNextLogin", false);
					startActivity(intent);
					break;
				case R.id.btn_key_hash_check:
					try {
						String packageName = getApplicationContext().getPackageName();

						PackageInfo info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

						for (Signature signature : info.signatures) {

							MessageDigest md = MessageDigest.getInstance("SHA");
							md.update(signature.toByteArray());
							showDialog(R.string.success, String.format("*Package Name : \n\t%s\n*Key Hash :\n\t%s", packageName, Base64.encodeToString(md.digest(), Base64.DEFAULT)));
						}
					} catch (PackageManager.NameNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					break;
				default:
			}
		}
	};
}
