package com.campmobile.android.sampleapp.task;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	public static interface PostHandler {
		public void handle();
	}

	ImageView bmImage;

	PostHandler postHandler;

	public DownloadImageTask(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	public DownloadImageTask(ImageView bmImage, PostHandler postHandler) {
		this.bmImage = bmImage;
		this.postHandler = postHandler;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		//pd.show();
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		InputStream in = null;
		try {
			in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		//pd.dismiss();
		bmImage.setImageBitmap(result);

		// 작업 완료 후 postHandler 가 있으면 수행
		if (postHandler != null) {
			postHandler.handle();
		}
	}
}