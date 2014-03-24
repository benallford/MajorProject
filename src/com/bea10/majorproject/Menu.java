package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

public class Menu extends Activity{

	Button camera, but2, undo;
	static ImageView iv;
	Button bright_filter, dark_filter, neon_filter, img_lib_but, crystal_lib,
			save_img;
	MediaPlayer buttonSound;
	Bitmap bmp, operation, img_cam;
	String imageNameForSDCard;
	int time = (int) System.currentTimeMillis();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		save_img.setEnabled(false);
		
		

		img_lib_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				buttonSound.start();
				Intent img_lib = new Intent("com.bea10.majorproject.Image_lib");
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
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
				save_img.setEnabled(true);

			}

		});

		dark_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				BitmapDrawable abmp = (BitmapDrawable) iv.getDrawable();
				bmp = abmp.getBitmap();
				dark(iv);

			}

		});

		bright_filter.setOnClickListener(new OnClickListener() {

			/*
			 * This inner class responds to user input on a filter button and
			 * changes the image to the type of filter (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {

				buttonSound.start();
				BitmapDrawable abmp = (BitmapDrawable) iv.getDrawable();
				bmp = abmp.getBitmap();
				bright(iv);
				// bright_filter.setEnabled(false);

			}

		});

		crystal_lib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				buttonSound.start();
				Intent crystalLib = new Intent(
						"com.bea10.majorproject.crystal_images");
				startActivity(crystalLib);

			}

		});

		save_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkSDCard();
				save_img.setEnabled(false);

			}
		});
		
		
	}
	
	

	private void initalise() {

		camera = (Button) findViewById(R.id.button1);
		iv = (ImageView) findViewById(R.id.picture);
		// undo = (Button) findViewById(R.id.undo);
		bright_filter = (Button) findViewById(R.id.Filter1);
		dark_filter = (Button) findViewById(R.id.Filter2);
		neon_filter = (Button) findViewById(R.id.Filter3);

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

		if (requestCode == 0) {

			img_cam = (Bitmap) data.getExtras().get("data");
			iv.setImageBitmap(img_cam);
			Toast.makeText(getApplicationContext(), "Choose a filter to add cool effects!", Toast.LENGTH_LONG).show();
		}

	}

	/*
	 * Method for adding a bright filter to an image in the image view
	 * container.
	 */
	public void bright(View view) {
		operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				bmp.getConfig());

		// Image consists of two-dimensional matrix, get the pixels from the
		// image
		for (int i = 0; i < bmp.getWidth(); i++) {
			for (int j = 0; j < bmp.getHeight(); j++) {
				int p = bmp.getPixel(i, j); // returns all the pixels in the
											// image and stores them in p
											// variable
				int r = Color.red(p);
				int g = Color.green(p);
				int b = Color.blue(p);
				int alpha = Color.alpha(p);

				r = 100 + r;
				g = 100 + g;
				b = 100 + b;
				alpha = 100 + alpha;

				operation.setPixel(i, j, Color.argb(alpha, r, g, b));
			}
		}
		iv.setImageBitmap(operation);
	}

	public void dark(View view) {
		operation = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				bmp.getConfig());

		for (int i = 0; i < bmp.getWidth(); i++) {
			for (int j = 0; j < bmp.getHeight(); j++) {
				int p = bmp.getPixel(i, j);
				int r = Color.red(p);
				int g = Color.green(p);
				int b = Color.blue(p);
				int alpha = Color.alpha(p);

				r = r - 50;
				g = g - 50;
				b = b - 50;
				alpha = alpha - 50;
				operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));

			}
		}

		iv.setImageBitmap(operation);
	}

	public void checkSDCard() {

		iv = (ImageView) findViewById(R.id.picture);
		BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable(); //convert imageview to bitmap so we can save it to external storage
		Bitmap bitmap = drawable.getBitmap();
		File sdCardDirectory = Environment.getExternalStorageDirectory();
		File image = new File(sdCardDirectory, time + ".png");

		boolean success = false;

		String state = Environment.getExternalStorageState(); //check whether SD card is available

		if (Environment.MEDIA_MOUNTED.equals(state)) { //if so then write to it
			FileOutputStream outStream;
			try {

				outStream = new FileOutputStream(image);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				/* 100 to keep full quality of the image */

				outStream.flush();
				outStream.close();
				success = true;
			} catch (FileNotFoundException e) {
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
		
		Toast.makeText(getApplicationContext(), "Click camera to take another photo!", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
