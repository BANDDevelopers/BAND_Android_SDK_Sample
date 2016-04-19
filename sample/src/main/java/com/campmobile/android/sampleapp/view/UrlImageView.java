/*
 * @(#)UrlImageView.java $version 2014. 2. 20.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.campmobile.android.sampleapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.campmobile.android.sampleapp.task.DownloadImageTask;
import com.campmobile.android.sampleapp.task.DownloadImageTask.PostHandler;

/**
 * @author Uicheon Hwang (dynang@campmobile.com )
 */
public class UrlImageView extends ImageView {

	private PostHandler postHandler = null;

	public UrlImageView(Context context) {
		this(context, null);
	}

	public UrlImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setPostHandler(PostHandler postHandler) {
		this.postHandler = postHandler;
	}

	public void setImageUrl(String url) {
		DownloadImageTask task = new DownloadImageTask(this, postHandler);
		task.execute(url);
	}
}
