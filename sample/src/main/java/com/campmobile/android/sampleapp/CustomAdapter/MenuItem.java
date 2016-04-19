package com.campmobile.android.sampleapp.CustomAdapter;

import android.view.View;

public class MenuItem {
	private String menuText;
	private boolean isTitle;
	private View.OnClickListener onClickListener;

	public MenuItem(String menuText, boolean isTitle, View.OnClickListener onClickListener) {
		this.menuText = menuText;
		this.isTitle = isTitle;
		this.onClickListener = onClickListener;
	}

	public String getMenuText() {
		return menuText;
	}

	public boolean isTitle() {
		return isTitle;
	}

	public View.OnClickListener getOnClickListener() {
		return onClickListener;
	}
}