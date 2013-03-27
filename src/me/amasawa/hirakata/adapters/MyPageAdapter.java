package me.amasawa.hirakata.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

public class MyPageAdapter extends PagerAdapter {
	List<View> pages = null;

	public MyPageAdapter(List<View> pages) {
		this.pages = pages;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View view = pages.get(position);
		((ViewPager) collection).addView(view, 0);
		return view;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount() {
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
}
