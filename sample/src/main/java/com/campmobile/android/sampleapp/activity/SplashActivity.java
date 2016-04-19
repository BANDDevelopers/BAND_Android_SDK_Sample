package com.campmobile.android.sampleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Profile;
import com.campmobile.android.sampleapp.R;

public class SplashActivity extends Activity {

	/**
	 * 마지막 프레임에서 로그인 액티비티 호출 여부
	 */
	private boolean isLastFrame = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		initBandManager();
	}

	private void initBandManager() {
		BandManager bandManager = BandManagerFactory.getSingleton();

		// profile API 를 호출하여 로그인 여부를 확인한다. 
		bandManager.getProfile(new ApiCallbacks<Profile>() {
			@Override
			public void onResponse(Profile profile) {
				// 로그인이 되어 있다면 커스텀 URL을 처리한다.
				handleCustomUrl();
			}

			@Override
			public void onPostExecute(boolean isSuccess) {
				super.onPostExecute(isSuccess);
				if (!isSuccess) {
					// 에러 발생시 로그인 페이지로 이동한다.
					playSplashClip(false);
				}
			}
		});
	}

	/**
	 * 커스텀 URL을 처리한다.
	 */
	private void handleCustomUrl() {
		try {

			Intent intent = getIntent();
			Uri uri = intent.getData();

			if (uri != null) {

				// 로그인이 되어 있는 경우 customUrl 이 있다면 처리한다. 
				if (uri != null) {
					String host = uri.getHost();

					if ("chat".equals(host)) {

						// 채팅 액티비티로 이동한다. 
						Intent newIntent = new Intent(SplashActivity.this, SendMessageActivity.class);
						newIntent.setData(uri);
						startActivity(newIntent);
						finish();
						return;

					} else if ("post".equals(host)) {

						// 포스팅 액티비티로 이동한다. 
						Intent newIntent = new Intent(SplashActivity.this, WritePostActivity.class);
						newIntent.setData(uri);
						startActivity(newIntent);
						finish();
						return;
					}
				}
			}

			// 스플래시 노출 후 메인으로 이동한다. 
			playSplashClip(true);

		} catch (Exception e) {
			// 에러 발생시 스플래시 노출 후 로그인 페이지로 이동한다.
			playSplashClip(false);
		}
	}

	/**
	 * 스플래시 이미지를 노출한다.
	 *
	 * @param isLogin
	 */
	public void playSplashClip(final boolean isLogin) {
		try {

			// splash 를 노출하기 위한 ImageView
			final ImageView splashImageView = (ImageView) findViewById(R.id.splash);
			final AnimationDrawable animationDrawable = (AnimationDrawable) splashImageView.getBackground();

			animationDrawable.setCallback(new Callback() {

				private Drawable lastFrame = animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1);

				@Override
				public void unscheduleDrawable(Drawable who, Runnable what) {
					splashImageView.unscheduleDrawable(who);
				}

				@Override
				public void scheduleDrawable(Drawable who, Runnable what, long when) {
					splashImageView.scheduleDrawable(who, what, when);
				}

				@Override
				public void invalidateDrawable(Drawable who) {
					splashImageView.invalidateDrawable(who);
					if (!isLastFrame && lastFrame.equals(who.getCurrent())) {
						isLastFrame = true;
						doAfterSplash(isLogin);
					}
				}
			});

			splashImageView.post(new Runnable() {

				@Override
				public void run() {
					animationDrawable.start();

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			doAfterSplash(isLogin);
		}

	}

	/**
	 * 로그인 여부에 따라 액티비티로 이동한다.
	 *
	 * @param isLogin
	 */
	private void doAfterSplash(boolean isLogin) {

		if (isLogin) {
			startMainActivityWithDelay();
		} else {
			startLoginActivityWithDelay();
		}
	}

	private void startLoginActivityWithDelay() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void startMainActivityWithDelay() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
		}); // 1초 뒤 종료
	}

}
