package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class Menu extends Activity {

	Button camera, but2, undo;
	static ImageView iv;
	Button bright_filter, dark_filter, neon_filter, img_lib_but, crystal_lib,
			save_img, gray_scale, reflection, round_corner, highlight;
	Drawable myDrawable;
	Editable value;

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
	ImageProcessor ip;
	public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;

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
		undo.setEnabled(false);
		bright_filter.setEnabled(false);
		dark_filter.setEnabled(false);
		neon_filter.setEnabled(false);
		gray_scale.setEnabled(false);
		reflection.setEnabled(false);
		round_corner.setEnabled(false);

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
				Context context = Menu.this;
				PackageManager packageManager = context.getPackageManager();
		 
				// if device support camera?
				if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					//yes
					//Log.i("camera", "This device has camera!");
					Intent intent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 0);
					save_img.setEnabled(true);
					bright_filter.setEnabled(true);
					dark_filter.setEnabled(true);
					neon_filter.setEnabled(true);
					gray_scale.setEnabled(true);
					reflection.setEnabled(true);
					round_corner.setEnabled(true);
					undo.setEnabled(true);
				}else{
					//no
					//Log.i("camera", "This device has no camera!");
					Toast.makeText(getApplicationContext(),
							"You need a Camera to use this feature!", Toast.LENGTH_SHORT)
							.show();
					
				}
				
				
			}

		});

		dark_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				BitmapDrawable drawable3 = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap3 = drawable3.getBitmap();
				Bitmap ok3 = doBrightness(bitmap3, -60);
				iv.setImageBitmap(ok3);
				undo.setEnabled(true);

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
				Bitmap ok3 = doBrightness(bitmap3, 50);
				iv.setImageBitmap(ok3);
				undo.setEnabled(true);

			}

		});

		crystal_lib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				buttonSound.start();
				Intent crystalLib = new Intent(
						"com.bea10.majorproject.crystal_images");
				startActivity(crystalLib);
				undo.setEnabled(false);

			}

		});

		save_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				buttonSound.start();
				checkSDCard();
				round_corner.setEnabled(true);
				Drawable myDrawable = getResources().getDrawable(
						R.drawable.press_cam);
				iv.setImageDrawable(myDrawable);
				undo.setEnabled(false);

			}

		});

		neon_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				buttonSound.start();
				BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
				Bitmap bitmap = drawable.getBitmap();
				Bitmap ok = doInvert(bitmap);
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
				Bitmap ok1 = doGreyscale(bitmap1);
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
				Bitmap ok2 = applyReflection(bitmap2);
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
				Bitmap ok2 = roundCorner(bitmap2, 45);
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

				Bitmap ok2 = getTintImage(bitmap2, 10);
				iv.setImageBitmap(ok2);

				undo.setEnabled(true);

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
		undo = (Button) findViewById(R.id.undo);
		bright_filter = (Button) findViewById(R.id.Filter1);
		dark_filter = (Button) findViewById(R.id.Filter2);
		neon_filter = (Button) findViewById(R.id.Filter3);
		gray_scale = (Button) findViewById(R.id.Filter4);
		reflection = (Button) findViewById(R.id.Filter5);
		round_corner = (Button) findViewById(R.id.Filter6);
		highlight = (Button) findViewById(R.id.Filter7);

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
					"Choose a filter to add cool effects!", Toast.LENGTH_SHORT)
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
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through every pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
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
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// pixel information
		int A, R, G, B;
		int pixel;

		// get image size
		int width = src.getWidth();
		int height = src.getHeight();

		// scan through every single pixel
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get one pixel color
				pixel = src.getPixel(x, y);
				// retrieve color of all channels
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// take conversion up to one single value
				R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
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
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public static Bitmap applyReflection(Bitmap originalImage) {
		// gap space between original and reflected
		final int reflectionGap = 4;
		// get image size
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// this will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// create a Bitmap with the flip matrix applied to it.
		// we only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// create a new bitmap with same width but taller to fit reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		// create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// draw in the gap
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// create a shader that is a linear gradient that covers the reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	public static Bitmap roundCorner(Bitmap src, float round) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create bitmap output
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		// set canvas for painting
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		// config paint
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);

		// config rectangle for embedding
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		// draw rect to canvas
		canvas.drawRoundRect(rectF, round, round, paint);

		// create Xfer mode
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// draw source image to canvas
		canvas.drawBitmap(src, rect, rect, paint);

		// return final image
		return result;
	}

	public static Bitmap applyShadingFilter(Bitmap source, int shadingColor) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// AND
				pixels[index] &= shadingColor;
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

public static Bitmap getTintImage(Bitmap src, int degree) {
        

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double) degree) / HALF_CIRCLE_DEGREE;

        int S = (int) (RANGE * Math.sin(angle));
        int C = (int) (RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = (pix[index] >> 16) & 0xff;
                int g = (pix[index] >> 8) & 0xff;
                int b = pix[index] & 0xff;
                RY = (70 * r - 59 * g - 11 * b) / 100;
                GY = (-30 * r + 41 * g - 11 * b) / 100;
                BY = (-30 * r - 59 * g + 89 * b) / 100;
                Y = (30 * r + 59 * g + 11 * b) / 100;
                RYY = (S * BY + C * RY) / 256;
                BYY = (C * BY - S * RY) / 256;
                GYY = (-51 * RYY - 19 * BYY) / 100;
                R = Y + RYY;
                R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
                G = Y + GYY;
                G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
                B = Y + BYY;
                B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
                pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
            }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
    }



	@Override
	protected void onResume() {

		super.onResume();

	}

}
