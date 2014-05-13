package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * 
 * This class allows us to load up two different image from the SD card and composite them 
 * 
 */
public class LoadImageFromSDCard extends Activity implements OnClickListener {

	static final int PICKED_ONE = 0;
	static final int PICKED_TWO = 1;
	boolean onePicked = false;
	boolean twoPicked = false;
	Button Picture1, Picture2, Save, roundCorner, reflect;

	ImageView ImageView;
	Bitmap bmp1, bmp2;

	Canvas canvas; // canvas so we can draw on
	Paint paint; // paint object to draw with

	int time = (int) System.currentTimeMillis(); // use current system time so
													// we can use it to save an
													// image uniquely

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard); // set layout to sdcard.xml
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // only
																			// allow
																			// the
																			// activity
																			// to
																			// be
																			// viewed
																			// in
																			// portrait

		initalise_buttons(); // initialise the buttons so we can use them
		Picture1.setOnClickListener(this);// listen out for activity
		Picture2.setOnClickListener(this);

		roundCorner.setEnabled(false);
		reflect.setEnabled(false); // set these two to false so the user can't
									// use them when there is no image in the
									// image view

		roundCorner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				/*
				 * Usual Bitmap objects and BitmapDrawable objects to get image
				 * in image view and work with etc
				 */

				BitmapDrawable drawable2 = (BitmapDrawable) ImageView
						.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();

				Bitmap ok2 = ImageEffects.roundCornerOfImage(bitmap2, 45);
				ImageView.setImageBitmap(ok2);
				roundCorner.setEnabled(false); // once effect has been applied
												// don't let user apply it again
			}

		});

		reflect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				BitmapDrawable drawable2 = (BitmapDrawable) ImageView
						.getDrawable();
				Bitmap bitmap2 = drawable2.getBitmap();

				Bitmap ok2 = ImageEffects.applyReflection(bitmap2);
				ImageView.setImageBitmap(ok2);
				reflect.setEnabled(false);
			}

		});

		Save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				checkSDCard(); // call method to save to SD card when button is
								// pressed.
				roundCorner.setEnabled(false);
				reflect.setEnabled(false);
				ImageView.setImageResource(R.drawable.ic_launcher); // change
																	// imageview
																	// to
																	// default
																	// android
																	// logo

			}

		});

	}

	public void onClick(View v) { // compare View object that is passed in to
									// determine which button was pressed
		int which = -1; // set to -1 otherwise if it was set to 0 PICKED_ONE
						// would pass by default
		if (v == Picture1) {
			which = PICKED_ONE;
		} else if (v == Picture2) {
			which = PICKED_TWO;
		}
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // so
																				// we
																				// can
																				// view
																				// SD
																				// card
																				// images
																				// in
																				// the
																				// gallery
		startActivityForResult(choosePictureIntent, which);
	}

	/*
	 * After the user has selected an image, our onActivityResult method is
	 * called. The variable that we passed in via the startActivityForResult
	 * method is passed back to us in the first parameter, which we are calling
	 * requestCode. Using this we know which image, the first or second, the
	 * user just chose. We use this value to decide which Bitmap object to load
	 * the chosen image into.
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Uri imageFileUri = intent.getData();
			if (requestCode == PICKED_ONE) {
				bmp1 = load_Bitmap_URI(imageFileUri);
				onePicked = true;
			} else if (requestCode == PICKED_TWO) {
				bmp2 = load_Bitmap_URI(imageFileUri);
				twoPicked = true;
			}
			/*
			 * When both images have been selected and both Bitmap objects have
			 * been instantiated, we can then move forward with our compositing
			 * operations.
			 */
			if (onePicked && twoPicked) {
				Bitmap drawingBitmap = Bitmap.createBitmap(bmp1.getWidth(),
						bmp1.getHeight(), bmp1.getConfig()); //create one new bitmap so images can be composite on to one another
				canvas = new Canvas(drawingBitmap); // call canvas object for drawing and use bitmap created above
				paint = new Paint();
				canvas.drawBitmap(bmp1, 0, 0, paint);  //draw on bitmap to canvas
				paint.setXfermode(new PorterDuffXfermode(
						android.graphics.PorterDuff.Mode.SCREEN)); /* Inverts each of the colors,
																	performs the same operation (multiplies them together and divides by
																		255), and then inverts once again. Result Color = 255 - (((255 - Top
																			Color) * (255 - Bottom Color)) / 255) */ 
				canvas.drawBitmap(bmp2, 0, 0, paint);
				ImageView.setImageBitmap(drawingBitmap); //set image view to bitmap that has been draw on 
				
				/* Enable filters so the user can add finishing touching effects to the image */
				roundCorner.setEnabled(true); 
				reflect.setEnabled(true);

			}
		}
	}

	/*
	 * a method to load a  Bitmap from a URI scaled to be no larger than the size of the screen.
	 */
	private Bitmap load_Bitmap_URI(Uri imageFileUri) {
		Display currentDisplay = getWindowManager().getDefaultDisplay();
		float dw = currentDisplay.getWidth();
		float dh = currentDisplay.getHeight();
		// ARGB_4444 is desired
		Bitmap returnBmp = Bitmap.createBitmap((int) dw, (int) dh,
				Bitmap.Config.ARGB_4444);
		try {
			// Load up the image's dimensions not the image itself
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = true;
			returnBmp = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(imageFileUri), null, bmpFactoryOptions);
			int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / dh);
			int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / dw);
			Log.v("HEIGHTRATIO", "" + heightRatio);
			Log.v("WIDTHRATIO", "" + widthRatio);
			// If both of the ratios are greater than 1, one of the sides of the
			// image is greater than the screen
			if (heightRatio > 1 && widthRatio > 1) {
				if (heightRatio > widthRatio) {
					// Height ratio is larger, scale according to it
					bmpFactoryOptions.inSampleSize = heightRatio;
				} else {
					// Width ratio is larger, scale according to it
					bmpFactoryOptions.inSampleSize = widthRatio;
				}
			}
			// Decode it for real
			bmpFactoryOptions.inJustDecodeBounds = false;
			returnBmp = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(imageFileUri), null, bmpFactoryOptions);
		} catch (FileNotFoundException e) {

			Log.v("ERROR", e.toString());
		}
		return returnBmp;
	}

	public void checkSDCard() {

		ImageView = (ImageView) findViewById(R.id.CompositeImageView);
		BitmapDrawable drawable = (BitmapDrawable) ImageView.getDrawable(); // convert
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

	}

	/* Find buttons by their ID's so we can use them */
	public void initalise_buttons() {

		ImageView = (ImageView) this.findViewById(R.id.CompositeImageView);
		Picture1 = (Button) this.findViewById(R.id.Picture_Button1);
		Picture2 = (Button) this.findViewById(R.id.Picture_Button2);
		Save = (Button) findViewById(R.id.SaveImg);
		roundCorner = (Button) findViewById(R.id.Filter20);
		reflect = (Button) findViewById(R.id.Filter21);

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, Menu.class)); // return to menu activity
														// when back button is
														// pressed
		finish();
	}

}