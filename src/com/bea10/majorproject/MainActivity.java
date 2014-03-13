package com.bea10.majorproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	MediaPlayer splashSound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		splashSound = MediaPlayer.create(this, R.raw.crush);
		splashSound.start();
		Thread splashTimer = new Thread(){
			
			public void run(){
				
				try {
					sleep(5000);
					Intent menuIntent = new Intent("com.bea10.majorproject.MENU");
				startActivity(menuIntent);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}finally{
					
					finish(); //finish current activity
				}
				
				
			}
			
		};
		
		splashTimer.start();
	}
	
	

	@Override
	protected void onPause() {
		splashSound.release();
		super.onPause();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
