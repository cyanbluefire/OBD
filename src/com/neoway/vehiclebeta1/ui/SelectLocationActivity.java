package com.neoway.vehiclebeta1.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsStatus.Listener;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.sug.*;
import com.baidu.mapapi.search.sug.*;
import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.utils.ActivityCollector;

public class SelectLocationActivity extends Activity{
	
//	private QueryDB queryDB = new QueryDB();
//	DbOfMyRoute dbOfMyRoute;	
	private AutoCompleteTextView autoTxt;
	protected static final String TAG = "selectLocation";
	ArrayList<String> arrayList_autoStrs = new ArrayList<String>();
	String location = "";
	private SuggestionSearch mSuggestionSearch = null;
	private SimpleAdapter adapter_recLocation;
	private ArrayAdapter<String> adapter_hisLocation;
	
	private List<Map<String, Object>> list_aoutoCom;
	private ListView lv_recLocation;
	private ListView lv_historyLocation;
	private TextView tv_noHistoryLocation;
	private List<String> history_location_list;
	private Button btn_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		Log.v(TAG,"start oncreate selectLocation");
		SDKInitializer.initialize(getApplicationContext());					//初始化sdk引用的context信息，必需在setContentView之前
		setContentView(R.layout.activity_select_location);	
		
		init();
		/*
		 * 建议查找
		 */
		OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
			
			@Override
			public void onGetSuggestionResult(SuggestionResult res) {
				// TODO Auto-generated method stub
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				list_aoutoCom.clear();
				for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("prompt", info.key);
						list_aoutoCom.add(map);
					}
				}
				adapter_recLocation.notifyDataSetChanged();
				Log.v(TAG,"notifyDataSetChanged");
			}
		};
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(listener);
		
		/*
		 * 显示百度推荐地点
		 */
		autoTxt = (AutoCompleteTextView)findViewById(R.id.input_location);
		list_aoutoCom = new ArrayList<Map<String,Object>>();
		adapter_recLocation = new SimpleAdapter(this, list_aoutoCom, R.layout.item_set_location, new String[]{"prompt"}, new int[]{R.id.tv_prompt});
		lv_recLocation = (ListView) findViewById(R.id.lv_prompt);
		lv_recLocation.setAdapter(adapter_recLocation);
		autoTxt.addTextChangedListener(watcher);
		lv_recLocation.setOnItemClickListener(itemlistener);

	}
	/*
	 * 初始化控件
	 */
	public void init(){
		btn_search = (Button)findViewById(R.id.btn_search);
		
		btn_search.setOnClickListener(clickListener);
	}
	public OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
			case R.id.btn_search:
				Intent intent = new Intent();
				intent.putExtra("type", "search_location");
				intent.putExtra("search_location", location);
				setResult(RESULT_OK,intent);
				finish();
				break;
			}
		}
	};
	/*
	 * 建议地点列表监听
	 */
	public OnItemClickListener itemlistener =  new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			hideInputMethod(arg1);	
			location = list_aoutoCom.get(position).get("prompt").toString();			
			Intent intent = new Intent();
			intent.putExtra("type", "suggestion_location");
			intent.putExtra("suggestion_location", location);
			setResult(RESULT_OK,intent);
			finish();
		}
		
	};
	/*
	 * 自动填充监听
	 */
	private TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
						
		}
		
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
//			lv_historyLocation.setVisibility(8);				//隐藏历史地点列表(不可见且不占原来布局空间)，只显示百度推荐地点列表
			location = arg0.toString();
			Log.v(TAG, "afterTextChanged "+location);
			mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
					 .city("深圳")
					 .keyword(location));
			
		}
	};


	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//hideInputMethod();
		super.onDestroy();
		mSuggestionSearch.destroy();
		ActivityCollector.removeActivity(this);
	}

	private void hideInputMethod(View view) {
		InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(manager.isActive()){
			manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
}
