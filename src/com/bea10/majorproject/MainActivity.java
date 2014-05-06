package com.bea10.majorproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;

/*
 * This class is the first activity that is launched which "runs" the splash screen and then launches the menu activity
 * 
 */
public class MainActivity extends Activity {

	MediaPlayer splashSound; //MediaPlayer object so we can use sound for the splash screen
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen); //start activity with splash_screen.xml file layout
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //only allow activity to be viewed in portrait 
		
		splashSound = MediaPlayer.create(this, R.raw.crush); // use sound from raw folder
		splashSound.start(); //play sound
		Thread splashTimer = new Thread(){ //create a thread object so we can play sound for certain length of time
			
			public void run(){
				
				try {
					sleep(5000); //delay starting of menu activity for 5 seconds and play sound
					Intent menuIntent = new Intent("com.bea10.majorproject.MENU"); //start menu activity
				startActivity(menuIntent);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}finally{
					
					finish(); //finish current activity
				}
				
				
			}
			
		};
		
		splashTimer.start();// start the thread above
	}
	
	

	@Override
	protected void onPause() {
		splashSound.release(); //release the sound so that it doesnt continue on playing 
		super.onPause();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
