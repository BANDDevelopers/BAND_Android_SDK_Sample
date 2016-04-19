package com.campmobile.android.sampleapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.AccessToken;
import com.campmobile.android.sampleapp.R;

public abstract class BaseToolbarActivity extends AppCompatActivity {
	@Override
	protected void onResume() {
		super.onResume();
		BandManagerFactory.getSingleton().getAuthManager().checkAndRefreshAccessToken(this, new ApiCallbacks<AccessToken>() {
			@Override
			public void onResponse(AccessToken response) {
			}

			@Override
			public void onError(VolleyError error) {
				super.onError(error);
				Toast.makeText(BaseToolbarActivity.this, "getAccessToken failed. " + error.getMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAuthFailure(VolleyError error) {
				super.onAuthFailure(error);
				startLoginActivity();
				finish();
			}
		});
	}

	private void startLoginActivity() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(BaseToolbarActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbarmenu, menu);

		if (this instanceof MainActivity) {
			menu.findItem(R.id.btn_home).setVisible(false);
		} else {
			menu.findItem(R.id.btn_setting).setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.btn_setting:
				Intent settingIntent = new Intent(BaseToolbarActivity.this, SettingActivity.class);
				startActivity(settingIntent);
				break;
			case R.id.btn_home:
				Intent homeIntent = new Intent(BaseToolbarActivity.this, MainActivity.class);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(homeIntent);
				finish();
				break;
			default:
				break;
		}
		return true;
	}

	protected void setToolbar(int rid) {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(toolbar != null) {
			toolbar.setTitle(rid);
			setNavigationIcon(toolbar);
			setSupportActionBar(toolbar);
		}
	}

	private void setNavigationIcon(Toolbar toolbar) {
		if (!(this instanceof MainActivity)) {
			toolbar.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
		}
	}

	protected void showMessage(int rid) {
		Toast.makeText(getApplicationContext(), rid, Toast.LENGTH_SHORT).show();
	}

	protected void showMessage(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	protected void showDialog(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		if(!title.isEmpty()) {
			alertDialogBuilder.setTitle(title);
		}
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.create().show();
		alertDialogBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	protected void showDialog(int titleRid, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(titleRid);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.create().show();
		alertDialogBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}
}
