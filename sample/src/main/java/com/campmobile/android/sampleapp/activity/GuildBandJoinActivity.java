package com.campmobile.android.sampleapp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.campmobile.android.bandsdk.BandManager;
import com.campmobile.android.bandsdk.BandManagerFactory;
import com.campmobile.android.bandsdk.api.ApiCallbacks;
import com.campmobile.android.sampleapp.R;

public class GuildBandJoinActivity extends BaseToolbarActivity {
	private static final int SELECT_PHOTO = 1004;

	private BandManager bandManager = null;

	private EditText bandKeyEditText;
	private EditText nameEditText;
	private Button selectProfileBtn;
	private ImageView profileImageView;
	private Button joinBandBtn;

	private Uri selectedImageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guild_band_join);

		initParam();
		initUI();
		setToolbar(R.string.title_guild_band_join);
	}

	private void initParam() {
		bandManager = BandManagerFactory.getSingleton();
	}

	private void initUI() {
		bandKeyEditText = (EditText) findViewById(R.id.band_key_edit_text);
		nameEditText = (EditText) findViewById(R.id.name_edit_text);
		joinBandBtn = (Button) findViewById(R.id.join_band_btn);
		selectProfileBtn = (Button) findViewById(R.id.select_profile_btn);
		profileImageView = (ImageView) findViewById(R.id.profile_image_view);

		joinBandBtn.setOnClickListener(onClickListener);
		selectProfileBtn.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.join_band_btn:
					String bandKey = bandKeyEditText.getText().toString();
					String name = nameEditText.getText().toString();
					File profileImageFile = null;

					if (bandKey.length() == 0 || name.length() == 0) {
						showMessage("Fill All Content");
						return;
					}

					if(selectedImageUri != null) {
						profileImageFile = new File(getRealPathFromURI(selectedImageUri));
					}

					bandManager.joinGuildBand(bandKey, name, profileImageFile, new ApiCallbacks<Void>() {
						@Override
						public void onResponse(Void response) {
							showDialog(R.string.success, "success");
							finish();
						}

						@Override
						public void onError(VolleyError error) {
							super.onError(error);
							showDialog(R.string.fail, "fail");
						}
					});
					break;
				case R.id.select_profile_btn:
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, SELECT_PHOTO);
					break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch(requestCode) {
			case SELECT_PHOTO:
				if(resultCode == RESULT_OK){
					selectedImageUri = imageReturnedIntent.getData();
					Bitmap selectedImage = null;
					InputStream imageStream = null;

					try {
						imageStream = getContentResolver().openInputStream(selectedImageUri);
						selectedImage = BitmapFactory.decodeStream(imageStream);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					if(selectedImage != null) {
						profileImageView.setImageBitmap(selectedImage);
					}
				}
				break;
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj,
			null, null, null);
		int column_index = cursor
			.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
