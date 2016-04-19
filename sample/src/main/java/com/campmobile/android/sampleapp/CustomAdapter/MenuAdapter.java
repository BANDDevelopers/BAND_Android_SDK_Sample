package com.campmobile.android.sampleapp.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.campmobile.android.sampleapp.R;

public class MenuAdapter extends BaseAdapter {
	private List<MenuItem> menuList;
	private LayoutInflater inflater;

	public MenuAdapter(Context context) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuList = new ArrayList<>();
	}

	public void clear() {
		this.menuList.clear();
	}

	public void addList(List<MenuItem> menuList) {
		this.menuList.addAll(menuList);
	}

	@Override
	public int getCount() {
		return menuList.size();
	}

	@Override
	public Object getItem(int index) {
		return menuList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_menu_list_item, parent, false);
		}

		MenuItem menuItem = menuList.get(position);
		RelativeLayout titleRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.title_relative_layout);
		RelativeLayout menuRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.menu_relative_layout);

		if(menuItem.isTitle()) {
			titleRelativeLayout.setVisibility(View.VISIBLE);
			menuRelativeLayout.setVisibility(View.GONE);

			TextView titleText = (TextView) convertView.findViewById(R.id.title_text);
			titleText.setText(menuItem.getMenuText());
		} else {
			titleRelativeLayout.setVisibility(View.GONE);
			menuRelativeLayout.setVisibility(View.VISIBLE);

			TextView buttonText = (TextView) convertView.findViewById(R.id.button_text);
			buttonText.setText(menuItem.getMenuText());
			convertView.setOnClickListener(menuItem.getOnClickListener());
			convertView.setBackgroundResource(R.drawable.white_gray_selector);
		}

		return convertView;
	}
}