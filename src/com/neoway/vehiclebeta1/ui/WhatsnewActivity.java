package com.neoway.vehiclebeta1.ui;

import java.util.ArrayList;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.utils.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class WhatsnewActivity extends Activity {
	private ViewPager mViewPager;	
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;
	
	private int currIndex = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_whatsnew_viewpager);
		Log.i("cyan", "WhatsnewActivity 1");
		mViewPager = (ViewPager)findViewById(R.id.whatsnew_viewpager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		//将要分页显示的View装入数组中
	    LayoutInflater mLi = LayoutInflater.from(this);
	    View view1 = mLi.inflate(R.layout.whats1, null);
	    View view2 = mLi.inflate(R.layout.whats2, null);
	    View view3 = mLi.inflate(R.layout.whats3, null);
	    
	  //每个页面的view数据
	    final ArrayList<View> views = new ArrayList<View>();
	    views.add(view1);
        views.add(view2);
        views.add(view3);
        
        //初始化下方滚动点
        mPage0 = (ImageView)findViewById(R.id.page0);
        mPage1 = (ImageView)findViewById(R.id.page1);
        mPage2 = (ImageView)findViewById(R.id.page2);
      //填充ViewPager的数据适配器
        PagerAdapter mpagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				container.removeView(views.get(position));
			}
			@Override
			public Object instantiateItem(View container, int position) {
				// TODO Auto-generated method stub
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		mViewPager.setAdapter(mpagerAdapter);
	}	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
		Log.i("cyan", "WhatsnewActivity Destroy");
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		
		public void onPageSelected(int arg0) {
			Log.i("cyan", "WhatsnewActivity 2");
			switch (arg0) {
			case 0:				
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				break;
			}
			currIndex = arg0;
			//animation.setFillAfter(true);// True:图片停在动画结束位置
			//animation.setDuration(300);
			//mPageImg.startAnimation(animation);
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
	/**
	 * 启动按钮
	 * @param v
	 */
	public void startbutton(View v) {  
		Log.i("cyan", "whatsnew start Welcome");
      	Intent intent = new Intent();
		intent.setClass(WhatsnewActivity.this,WelcomeActivity.class);
		startActivity(intent);
		finish();
      }  
	
		
}
