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

public class LoadImageFromSDCard extends Activity implements OnClickListener {

	static final int PICKED_ONE = 0;
	static final int PICKED_TWO = 1;
	boolean onePicked = false;
	boolean twoPicked = false;
	Button Picture1, Picture2, Save, roundCorner, reflect, hollow_effect;

	ImageView compositeImageView;
	Bitmap bmp1, bmp2;

	Canvas canvas;
	Paint paint;

	int time = (int) System.currentTimeMillis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initalise_buttons();
		Picture1.setOnClickListener(this);
		Picture2.setOnClickListener(this);

		roundCorner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});

		reflect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});

		hollow_effect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}

		});

		Save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				checkSDCard();

			}

		});

	}

	public void onClick(View v) {
		int which = -1;
		if (v == Picture1) {
			which = PICKED_ONE;
		} else if (v == Picture2) {
			which = PICKED_TWO;
		}
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(choosePictureIntent, which);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Uri imageFileUri = intent.getData();
			if (requestCode == PICKED_ONE) {
				bmp1 = loadBitmap(imageFileUri);
				onePicked = true;
			} else if (requestCode == PICKED_TWO) {
				bmp2 = loadBitmap(imageFileUri);
				twoPicked = true;
			}
			if (onePicked && twoPicked) {
				Bitmap drawingBitmap = Bitmap.createBitmap(bmp1.getWidth(),
						bmp1.getHeight(), bmp1.getConfig());
				canvas = new Canvas(drawingBitmap);
				paint = new Paint();
				canvas.drawBitmap(bmp1, 0, 0, paint);
				paint.setXfermode(new PorterDuffXfermode(
						android.graphics.PorterDuff.Mode.SCREEN));
				canvas.drawBitmap(bmp2, 0, 0, paint);
				compositeImageView.setImageBitmap(drawingBitmap);
			}
		}
	}

	private Bitmap loadBitmap(Uri imageFileUri) {
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

		compositeImageView = (ImageView) findViewById(R.id.CompositeImageView);
		BitmapDrawable drawable = (BitmapDrawable) compositeImageView
				.getDrawable(); // convert
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

	public void initalise_buttons() {

		compositeImageView = (ImageView) this
				.findViewById(R.id.CompositeImageView);
		Picture1 = (Button) this.findViewById(R.id.Picture_Button1);
		Picture2 = (Button) this.findViewById(R.id.Picture_Button2);
		Save = (Button) findViewById(R.id.SaveImg);
		roundCorner = (Button) findViewById(R.id.Filter20);
		reflect = (Button) findViewById(R.id.Filter21);
		hollow_effect = (Button) findViewById(R.id.Filter22);

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, Menu.class));
		finish();
	}

}