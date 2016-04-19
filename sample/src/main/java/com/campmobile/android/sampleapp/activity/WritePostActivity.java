package com.campmobile.android.sampleapp.activity;

import static com.campmobile.android.sampleapp.SampleConstants.RequestCode.BAND_SELECT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.Band;
import com.campmobile.android.sampleapp.R;
import com.campmobile.android.sampleapp.SampleConstants;

public class WritePostActivity extends BaseToolbarActivity {

	private BandManager bandManager;

	private TextView bandNameTextView;
	private EditText bodyEditText;
	private EditText imageEditText;
	private EditText subTitleEditText;
	private EditText subTextEditText;
	private EditText linkButtonTextEditText;
	private EditText androidCustomUrlEditText;
	private EditText iosCustomUrlEditText;
	private Button writePostButton;

	private Band selectedBand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_post);

		initParam();
		initUI();
		setToolbar(R.string.title_write_post);
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();
	}

	private void initUI() {
		bandNameTextView = (TextView) findViewById(R.id.band_name_text_view);
		bandNameTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WritePostActivity.this, BandListActivity.class);
				startActivityForResult(intent, SampleConstants.RequestCode.BAND_SELECT);
			}
		});
		bodyEditText = (EditText) findViewById(R.id.body_edit_text);
		imageEditText = (EditText) findViewById(R.id.image_edit_text);
		subTitleEditText = (EditText) findViewById(R.id.sub_title_edit_text);
		subTextEditText = (EditText) findViewById(R.id.sub_text_edit_text);
		linkButtonTextEditText = (EditText) findViewById(R.id.link_button_text_edit_text);
		androidCustomUrlEditText = (EditText) findViewById(R.id.android_custom_url_edit_text);
		iosCustomUrlEditText = (EditText) findViewById(R.id.ios_custom_url_edit_text);
		writePostButton = (Button) findViewById(R.id.write_post_button);
		writePostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				writePost();
			}
		});
	}

	private void writePost() {

		if (selectedBand == null) {
			showMessage(R.string.write_post_select_band);
			return;
		}

		String bandKey = selectedBand.getBandKey();
		String body = bodyEditText.getText().toString();
		String imageUrl = imageEditText.getText().toString();
		String subTitle = subTitleEditText.getText().toString();
		String subText = subTextEditText.getText().toString();
		String linkButtonText = linkButtonTextEditText.getText().toString();
		String androidCustomUrl = androidCustomUrlEditText.getText().toString();
		String iosCustomUrl = iosCustomUrlEditText.getText().toString();

		bandManager.writePost(bandKey, body, imageUrl, subTitle, subText, linkButtonText, androidCustomUrl, iosCustomUrl, new ApiCallbacks<Void>() {
			@Override
			public void onResponse(Void result) {
				showMessage(R.string.success);
			}

			@Override
			public void onError(VolleyError error) {
				super.onError(error);
				showMessage(error.getMessage());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case BAND_SELECT:
				if (resultCode == RESULT_OK) {
					selectedBand = data.getParcelableExtra(SampleConstants.ParameterKey.BAND);
					bandNameTextView.setText(selectedBand.getName());
				}
				break;
		}
	}
}
