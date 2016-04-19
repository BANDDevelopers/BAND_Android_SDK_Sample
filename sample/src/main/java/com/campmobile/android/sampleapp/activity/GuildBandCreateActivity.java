package com.campmobile.android.sampleapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.bandsdk.entity.BandKey;
import com.campmobile.android.sampleapp.R;

public class GuildBandCreateActivity extends BaseToolbarActivity {
	private BandManager bandManager = null;
	private TextView bandNameText;
	private Button createBandBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guild_band_create);

		initParam();
		initUI();
		setToolbar(R.string.title_create_guild_band);
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();
	}

	private void initUI() {
		bandNameText = (TextView) findViewById(R.id.band_name_text);
		createBandBtn = (Button) findViewById(R.id.create_band_btn);

		createBandBtn.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.create_band_btn:
					if (bandNameText.getText().length() == 0) {
						showMessage(R.string.guild_band_create_enter_value);
						return;
					}

					bandManager.createGuildBand(bandNameText.getText().toString(), new ApiCallbacks<BandKey>() {
						@Override
						public void onResponse(BandKey bandKey) {
							bandNameText.setText("");
							showDialog(R.string.success, "bandKey : " + bandKey.getBandKey());
						}

						@Override
						public void onError(VolleyError error) {
							super.onError(error);
							showDialog(R.string.error, error.getMessage());
						}
					});
					break;
			}
		}
	};
}
