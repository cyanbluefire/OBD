package com.neoway.vehiclebeta1.ui;



import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.utils.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {
	private ImageView im_shang;
	private ImageView im_xia;
	private ImageView im_ka;
	private ImageView im_ban;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcom);
		Log.i("cyan", "WelcomeActivity 1");
		im_shang = (ImageView)findViewById(R.id.image_shang);
		im_xia = (ImageView)findViewById(R.id.image_xia);
		im_ka = (ImageView)findViewById(R.id.image_ka);
		im_ban = (ImageView)findViewById(R.id.image_ban);
		
		//"上"的平移
		AnimationSet anim_shang = new AnimationSet(true);
		TranslateAnimation translateanim_shang = new TranslateAnimation(0,0,0,450);
		translateanim_shang.setDuration(1000);						//动画持续时间
		anim_shang.setStartOffset(800);							//动画开始的时间
		anim_shang.addAnimation(translateanim_shang);
		anim_shang.setFillAfter(true);								//动画结束时停留在之后的位置
		//"上"的淡化
		AlphaAnimation alphaAnim_shang = new AlphaAnimation(1,0);
		alphaAnim_shang.setDuration(2000);
		//alphaAnim_shang.setStartOffset(2000);
		anim_shang.addAnimation(alphaAnim_shang);
		im_shang.startAnimation(anim_shang);
		
		//"下"的平移
		AnimationSet anim_xia = new AnimationSet(true);
		TranslateAnimation translateanim_xia = new TranslateAnimation(0,0,0,-500);
		translateanim_xia.setDuration(1000);
		anim_xia.setStartOffset(800);
		anim_xia.addAnimation(translateanim_xia);
		anim_xia.setFillAfter(true);
		//"下'的淡化
		AlphaAnimation alphaAnim_xia = new AlphaAnimation(1,0);
		alphaAnim_xia.setDuration(2000);
		//alphaAnim_xia.setStartOffset(2000);
		anim_xia.addAnimation(alphaAnim_xia);
		im_xia.startAnimation(anim_xia);
		
		//"班"的旋转
		AnimationSet anim_ban = new AnimationSet(true);		
		RotateAnimation rotateAnim_ban = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim_ban.setDuration(2000);
		anim_ban.addAnimation(rotateAnim_ban);
		//"班"的平移
		TranslateAnimation translateAnim_ban = new TranslateAnimation(0, -100, 0, 0);
		translateAnim_ban.setDuration(3000);
		anim_ban.addAnimation(translateAnim_ban);
		anim_ban.setFillAfter(true);
		im_ban.setAnimation(anim_ban);
		
		//"卡"的重现
		AnimationSet anim_ka = new AnimationSet(true);
		AlphaAnimation alphaAnim_ka = new AlphaAnimation(0, 1);
		alphaAnim_ka.setDuration(2000);
		alphaAnim_ka.setStartOffset(2000);
		anim_ka.addAnimation(alphaAnim_ka);
		anim_ka.setFillAfter(true);
		//“卡”的平移
		TranslateAnimation translateAnim_ka = new TranslateAnimation(0,100,0,0);
		translateAnim_ka.setDuration(1000);
		translateAnim_ka.setStartOffset(2000);
		anim_ka.addAnimation(translateAnim_ka);
		im_ka.setAnimation(anim_ka);
		
		new Handler().postDelayed(new Runnable(){
			
			public void run(){
				Intent intent = new Intent (WelcomeActivity.this,MainActivity.class);			
				startActivity(intent);
				Log.i("WelcomeActivity", "go to MainActivity");
				WelcomeActivity.this.finish();
			}
		}, 3000);				
		
	}
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	ActivityCollector.removeActivity(this);
}

}
