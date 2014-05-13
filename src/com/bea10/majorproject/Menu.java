package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

/*
 * The Menu class is the "home screen" of the app and contains the functions to let the user
 * use the camera, add effects to user taken images, save their image, access the crystal images
 * and access the image library
 */

public class Menu extends Activity {

	
	/*
	Standard initialisation of buttons 
	*/
	Button camera, but2, undo;
	static ImageView iv;
	Button bright_filter, dark_filter, neon_filter, img_lib_but, crystal_lib,
			save_img, gray_scale, reflection, round_corner, highlight, 
			
			shading; 
	Drawable myDrawable;
	Editable value;

	int draw[] = {R.drawable.image1,
	  R.drawable.image2,
	  R.drawable.image3,
	  R.drawable.image4,
	  R.drawable.image5,
	  R.drawable.image6,
	  R.drawable.image7,
	  R.drawable.image8,
	 R.drawable.image9,
 R.drawable.image10,
	 R.drawable.image11}; //array to store crystal images

	MediaPlayer buttonSound; //MediaPlayer object so that we can use sounds for button clicks
	Bitmap bmp, operation, img_cam; //Use of the Bitmap class to store images such as user taken ones
	String imageNameForSDCard; //image save location
	int time = (int) System.currentTimeMillis(); //random object to save images to sd card with current system time
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); //set view to acitvity_main.xml file
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //only allow the activity to be viewed in portrait mode
		
		

		buttonSound = MediaPlayer.create(this, R.raw.button_click);// creating
																	// an object
																	// from the
																	// MediaPlayer
																	// class so
																	// I can use
																	// sounds
																	// for
																	// button
																	// clicks
		
		
		
		initalise(); // get buttons etc
		
		/* Disable buttons at start up so the user can not apply image effects to empty image view   */
		save_img.setEnabled(false);
		undo.setEnabled(false);
		bright_filter.setEnabled(false);
		dark_filter.setEnabled(false);
		neon_filter.setEnabled(false);
		gray_scale.setEnabled(false);
		reflection.setEnabled(false);
		round_corner.setEnabled(false);
		highlight.setEnabled(false);
		shading.setEnabled(false);
		

		img_lib_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				buttonSound.start(); //call button sound
				Intent img_lib = new Intent("com.bea10.majorproject.sdcard"); //launch image library activity on click
				startActivity(img_lib);

			}

		});
		
		

		camera.setOnClickListener(new OnClickListener() {

			/*
			 * Listens for user input on the "camera" button and launches the
			 * camera activity when pressed (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {

				buttonSound.start();
				Context context = Menu.this;
				PackageManager packageManager = context.getPackageManager(); //Class for retrieving various kinds of information related to the application packages that are currently 
																			 //installed on the device

				// if device supports camera
				if (packageManager
						.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					
					Intent intent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 0);
					
					/* Enabled the image effect buttons once the user has taken an image */
					save_img.setEnabled(true);
					bright_filter.setEnabled(true);
					dark_filter.setEnabled(true);
					neon_filter.setEnabled(true);
					gray_scale.setEnabled(true);
					reflection.setEnabled(true);
					round_corner.setEnabled(true);
					undo.setEnabled(true);
					highlight.setEnabled(true);
					shading.setEnabled(true);

				} else {
					//if device doesnt have camera inform the user
					Toast.makeText(getApplicationContext(),
							"You need a Camera to use this feature!",
							Toast.LENGTH_SHORT).show();

				}

			}

		});

		dark_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				BitmapDrawable drawable3 = (BitmapDrawable) iv.getDrawable(); //bitmap drawable so we can draw on to a canvas
				Bitmap bitmap3 = drawable3.getBitmap(); //convert bitmapdrawable to bitmap
				Bitmap ok3 = ImageEffects.increaseAndDecreaseBrightness(bitmap3, -60); //apply image effect to a new bitmap object while passing in bitmap from imageview
				iv.setImageBitmap(ok3); //set image view to bitmap object
				undo.setEnabled(true); //enable undo button so the user can undo any effects etc

			}

		});

		bright_filter.setOnClickListener(new OnClickListener() {

			/*
			 * This inner class responds to user input on a filter button and
			 * changes the image to the type of filter i.e. bright filter (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {

				buttonSound.start();
				BitmapDrawable drawable3 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap3 = drawable3.getBitmap();
				Bitmap ok3 = ImageEffects.increaseAndDecreaseBrightness(bitmap3, 50);
				iv.setImageBitmap(ok3);
				undo.setEnabled(true);

			}

		});

		crystal_lib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				buttonSound.start();
				Intent crystalLib = new Intent(
						"com.bea10.majorproject.crystal_images"); //launch crystal image activity
				startActivity(crystalLib);
				undo.setEnabled(false);

			}

		});

		save_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				checkSDCard(); //call this method so save image to SD card
				round_corner.setEnabled(true); //enabled round corner effect 
				Drawable myDrawable = getResources().getDrawable(
						R.drawable.press_cam); 
				iv.setImageDrawable(myDrawable); //change imageview to image in drawable folder so user image is not still there
				undo.setEnabled(false);

			}

		});

		neon_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				buttonSound.start();
				BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap = drawable.getBitmap();
				Bitmap ok = ImageEffects.invertImageColours(bitmap);
				iv.setImageBitmap(ok);
				undo.setEnabled(true);

			}

		});

		gray_scale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				buttonSound.start();
				BitmapDrawable drawable1 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap1 = drawable1.getBitmap();
				Bitmap ok1 = ImageEffects.blackAndWhite(bitmap1);
				iv.setImageBitmap(ok1);
				undo.setEnabled(true);

			}

		});

		reflection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				buttonSound.start();

				BitmapDrawable drawable2 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();
				Bitmap ok2 = ImageEffects.applyReflection(bitmap2);
				iv.setImageBitmap(ok2);
				undo.setEnabled(true);
				reflection.setEnabled(false);

			}

		});

		undo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				iv.setImageBitmap(img_cam);
				reflection.setEnabled(true);

			}

		});

		round_corner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				BitmapDrawable drawable2 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();
				Bitmap ok2 = ImageEffects.roundCornerOfImage(bitmap2, 45);
				iv.setImageBitmap(ok2);
				undo.setEnabled(true);

			}

		});

		highlight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();

				BitmapDrawable drawable2 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();

				Bitmap ok2 = ImageEffects.getTintImage(bitmap2, 30);
				iv.setImageBitmap(ok2);

				undo.setEnabled(true);

			}
		});

		shading.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				BitmapDrawable drawable2 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();

				Bitmap ok2 = ImageEffects.shadeImage(bitmap2, -7500);
				iv.setImageBitmap(ok2);

				undo.setEnabled(true);

			}

		});
		
	}

		
/*
 * Method to initialise the buttons by finding them by their id in the xml files so we are able to use them in the app
 */
	private void initalise() {

		camera = (Button) findViewById(R.id.Camera);
		iv = (ImageView) findViewById(R.id.picture);
		undo = (Button) findViewById(R.id.undo);
		bright_filter = (Button) findViewById(R.id.Filter1);
		dark_filter = (Button) findViewById(R.id.Filter2);
		neon_filter = (Button) findViewById(R.id.Filter3);
		gray_scale = (Button) findViewById(R.id.Filter4);
		reflection = (Button) findViewById(R.id.Filter5);
		round_corner = (Button) findViewById(R.id.Filter6);
		highlight = (Button) findViewById(R.id.Filter7);
		shading = (Button) findViewById(R.id.Filter8);



		crystal_lib = (Button) findViewById(R.id.crystal_img);

		img_lib_but = (Button) findViewById(R.id.img_lib_button);

		save_img = (Button) findViewById(R.id.save_img);

	}

	/*
	 * Method for invoking the camera and returning the image taken by the
	 * camera in to an image view on the main menu (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) { //if camera is available

			img_cam = (Bitmap) data.getExtras().get("data"); //return the image back from devices camera
			iv.setImageBitmap(img_cam); //set user taken image to the image view container
			Toast.makeText(getApplicationContext(),
					"Choose a filter to add cool effects!", Toast.LENGTH_SHORT)
					.show(); //pop-up to instruct user

		}

	}

	
/*
 * Method for checking that the SD card is available and then allowing an image to be saved to it
 * 
 */
	public void checkSDCard() {

		iv = (ImageView) findViewById(R.id.picture);
		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable(); // convert
																		// imageview
																		// to
																		// bitmap
																		// so we
																		// can
																		// save
																		// it to
																		// external
																		// storage
		Bitmap bitmap = drawable.getBitmap();
		File sdCardDirectory = Environment.getExternalStorageDirectory(); //get the directory of the SD card so we can save to it
		File image = new File(sdCardDirectory, time + ".png"); //create a filer with the SD card directory, the current system time and
																// .png so the image can be saved in a location under a file name

		boolean success = false;

		String state = Environment.getExternalStorageState(); // check whether
																// SD card is
																// available

		if (Environment.MEDIA_MOUNTED.equals(state)) { // if available
			FileOutputStream outStream; 
			try {

				outStream = new FileOutputStream(image); //export image to SD card directory
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream); //compress the image
				/* 100 to keep full quality of the image */

				outStream.flush();
				outStream.close(); //always make sure to close the stream to avoid memory leaks etc
				success = true;
			} catch (FileNotFoundException e) { //generic error handling if SD card is not available 
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (success) {
				Toast.makeText(getApplicationContext(),
						"Image saved with success", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"Error during image saving", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "SD card is not available",
					Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(getApplicationContext(),
				"Click camera to take another photo!", Toast.LENGTH_SHORT)
				.show();

	}

	
		
	

	@Override
	public void onBackPressed() {

		finish();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent); //end all activities and return to home screen 

	}

	
	
	

}
