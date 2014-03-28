package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class Menu extends Activity {

	Button camera, but2, undo;
	static ImageView iv;
	Button bright_filter, dark_filter, neon_filter, img_lib_but, crystal_lib,
			save_img, highlight, gray_scale, hue_filter;
	MediaPlayer buttonSound;
	Bitmap bmp, operation, img_cam;
	String imageNameForSDCard;
	int time = (int) System.currentTimeMillis();
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF startPoint = new PointF();
	PointF midPoint = new PointF();
	float oldDist = 1f;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

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
				Intent img_lib = new Intent("com.bea10.majorproject.sdcard");
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
				BitmapDrawable drawable3 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap3 = drawable3.getBitmap();
				Bitmap ok3 = doBrightness(bitmap3,-60);
				iv.setImageBitmap(ok3);

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
				BitmapDrawable drawable3 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap3 = drawable3.getBitmap();
				Bitmap ok3 = doBrightness(bitmap3,50);
				iv.setImageBitmap(ok3);

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
			public void onClick(View v) {
				buttonSound.start();
				checkSDCard();
				save_img.setEnabled(false);

			}

		});
		
		neon_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//iv.buildDrawingCache();
				buttonSound.start();
				BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap = drawable.getBitmap();
				Bitmap ok = doInvert(bitmap);
				iv.setImageBitmap(ok);

			}

		});
		
		gray_scale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//iv.buildDrawingCache();
				buttonSound.start();
				BitmapDrawable drawable1 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap1 = drawable1.getBitmap();
				Bitmap ok1 = doGreyscale(bitmap1);
				iv.setImageBitmap(ok1);

			}

		});
		
		hue_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//iv.buildDrawingCache();
				buttonSound.start();
				BitmapDrawable drawable2 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();
				//Bitmap ok2 = applySaturationFilter(bitmap2,1);
				//iv.setImageBitmap(ok2);

			}

		});

		iv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				System.out.println("matrix=" + savedMatrix.toString());
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					startPoint.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(midPoint, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - startPoint.x,
								event.getY() - startPoint.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, midPoint.x,
									midPoint.y);
						}
					}
					break;
				}
				view.setImageMatrix(matrix);
				return true;
			}

			@SuppressLint("FloatMath")
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
	}

	private void initalise() {

		camera = (Button) findViewById(R.id.Camera);
		iv = (ImageView) findViewById(R.id.picture);
		// undo = (Button) findViewById(R.id.undo);
		bright_filter = (Button) findViewById(R.id.Filter1);
		dark_filter = (Button) findViewById(R.id.Filter2);
		neon_filter = (Button) findViewById(R.id.Filter3);
		gray_scale = (Button) findViewById(R.id.Filter4);
		hue_filter = (Button) findViewById(R.id.Filter5);

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
			Toast.makeText(getApplicationContext(),
					"Choose a filter to add cool effects!", Toast.LENGTH_LONG)
					.show();
		}

	}

	/*
	 * Method for adding a bright filter to an image in the image view
	 * container.
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
		File sdCardDirectory = Environment.getExternalStorageDirectory();
		File image = new File(sdCardDirectory, time + ".png");

		boolean success = false;

		String state = Environment.getExternalStorageState(); // check whether
																// SD card is
																// available

		if (Environment.MEDIA_MOUNTED.equals(state)) { // if so then write to it
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

		Toast.makeText(getApplicationContext(),
				"Click camera to take another photo!", Toast.LENGTH_SHORT)
				.show();

	}
	
	public static Bitmap doInvert(Bitmap src) {
	    // create new bitmap with the same settings as source bitmap
	    Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
	    // color info
	    int A, R, G, B;
	    int pixelColor;
	    // image size
	    int height = src.getHeight();
	    int width = src.getWidth();
	 
	    // scan through every pixel
	    for (int y = 0; y < height; y++)
	    {
	        for (int x = 0; x < width; x++)
	        {
	            // get one pixel
	            pixelColor = src.getPixel(x, y);
	            // saving alpha channel
	            A = Color.alpha(pixelColor);
	            // inverting byte for each R/G/B channel
	            R = 255 - Color.red(pixelColor);
	            G = 255 - Color.green(pixelColor);
	            B = 255 - Color.blue(pixelColor);
	            // set newly-inverted pixel to output image
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	 
	    // return final bitmap
	    return bmOut;
	}
	
	public static Bitmap doGreyscale(Bitmap src) {
	    // constant factors
	    final double GS_RED = 0.299;
	    final double GS_GREEN = 0.587;
	    final double GS_BLUE = 0.114;
	 
	    // create output bitmap
	    Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
	    // pixel information
	    int A, R, G, B;
	    int pixel;
	 
	    // get image size
	    int width = src.getWidth();
	    int height = src.getHeight();
	 
	    // scan through every single pixel
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            // get one pixel color
	            pixel = src.getPixel(x, y);
	            // retrieve color of all channels
	            A = Color.alpha(pixel);
	            R = Color.red(pixel);
	            G = Color.green(pixel);
	            B = Color.blue(pixel);
	            // take conversion up to one single value
	            R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
	            // set new pixel color to output bitmap
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	 
	    // return final image
	    return bmOut;
	}
	
	public static Bitmap doBrightness(Bitmap src, int value) {
	    // image size
	    int width = src.getWidth();
	    int height = src.getHeight();
	    // create output bitmap
	    Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
	    // color information
	    int A, R, G, B;
	    int pixel;
	 
	    // scan through all pixels
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            // get pixel color
	            pixel = src.getPixel(x, y);
	            A = Color.alpha(pixel);
	            R = Color.red(pixel);
	            G = Color.green(pixel);
	            B = Color.blue(pixel);
	 
	            // increase/decrease each channel
	            R += value;
	            if(R > 255) { R = 255; }
	            else if(R < 0) { R = 0; }
	 
	            G += value;
	            if(G > 255) { G = 255; }
	            else if(G < 0) { G = 0; }
	 
	            B += value;
	            if(B > 255) { B = 255; }
	            else if(B < 0) { B = 0; }
	 
	            // apply new pixel color to output bitmap
	            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	 
	    // return final image
	    return bmOut;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
