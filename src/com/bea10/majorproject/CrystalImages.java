package com.bea10.majorproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class CrystalImages extends Menu {


	private Integer[] mImageIds = { R.drawable.image1, R.drawable.image2,
			R.drawable.image3, R.drawable.image4, R.drawable.image5,
			R.drawable.image6, R.drawable.image7, R.drawable.image8,
			R.drawable.image9, R.drawable.image10, R.drawable.image11 };
	


	Bitmap bi;
	InputStream is;
	ImageView iv1;
	Button dark_filter1, bright_filter1, save_img1, neon_filter1, gray_scale2,
			undo1, reflection2, round_corner1;
	int time = (int) System.currentTimeMillis();
	private Bitmap operation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crystal_images);
		
		initalise_crystal_buttons();
		
		Toast.makeText(getApplicationContext(), "Select an image to get started!",
				Toast.LENGTH_SHORT).show();
		
		dark_filter1.setEnabled(false);
		bright_filter1.setEnabled(false);
		save_img1.setEnabled(false);
		neon_filter1.setEnabled(false);
		gray_scale2.setEnabled(false);
		reflection2.setEnabled(false);
		undo1.setEnabled(false);
		round_corner1.setEnabled(false);

		// BitmapDrawable abmp = (BitmapDrawable)selectedImage.getDrawable();
		// bi = abmp.getBitmap();

		// InputStream is = getResources().openRawResource(R.drawable.image1);
		// bi = BitmapFactory.decodeStream(is);

		final Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		iv1 = (ImageView) findViewById(R.id.crystal_img_imgV);
		gallery.setSpacing(1);
		gallery.setAdapter(new GalleryImageAdapter(this));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// show the selected Image
				iv1.setImageResource(mImageIds[position]);
				dark_filter1.setEnabled(true);
				bright_filter1.setEnabled(true);
				save_img1.setEnabled(true);
				neon_filter1.setEnabled(true);
				gray_scale2.setEnabled(true);
				reflection2.setEnabled(true);
				round_corner1.setEnabled(true);

			}
		});

		

		dark_filter1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				BitmapDrawable drawable4 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap3 = drawable4.getBitmap();
				Bitmap ok4 = doBrightness(bitmap3, -60);
				iv1.setImageBitmap(ok4);
				undo1.setEnabled(true);

			}

		});

		

		bright_filter1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				BitmapDrawable drawable5 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap4 = drawable5.getBitmap();
				Bitmap ok5 = doBrightness(bitmap4, 50);
				iv1.setImageBitmap(ok5);
				undo1.setEnabled(true);

			}

		});

		
		neon_filter1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				BitmapDrawable drawable6 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap5 = drawable6.getBitmap();
				Bitmap ok6 = doInvert(bitmap5);
				iv1.setImageBitmap(ok6);
				undo1.setEnabled(true);

			}

		});

		
		gray_scale2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				BitmapDrawable drawable7 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap6 = drawable7.getBitmap();
				Bitmap ok7 = doGreyscale(bitmap6);
				iv1.setImageBitmap(ok7);
				undo1.setEnabled(true);

			}

		});

		
		reflection2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				BitmapDrawable drawable8 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap7 = drawable8.getBitmap();
				Bitmap ok8 = applyReflection(bitmap7);
				iv1.setImageBitmap(ok8);
				reflection2.setEnabled(false);
				undo1.setEnabled(true);

			}

		});

		

		save_img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				buttonSound.start();
				checkSDCard();
				reflection2.setEnabled(true);
				Drawable myDrawable = getResources().getDrawable(R.drawable.undo_image);
				iv1.setImageDrawable(myDrawable);
				round_corner1.setEnabled(true);

			}

		});
		
		undo1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				buttonSound.start();
				Drawable myDrawable = getResources().getDrawable(R.drawable.undo_image);
				iv1.setImageDrawable(myDrawable);
				reflection2.setEnabled(true);
			}

		});
		
		round_corner1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				buttonSound.start();
				BitmapDrawable drawable9 = (BitmapDrawable) iv1.getDrawable();
				Bitmap bitmap8 = drawable9.getBitmap();
				Bitmap ok9 = roundCorner(bitmap8,45);
				iv1.setImageBitmap(ok9);
				round_corner.setEnabled(false);
				
				
			}

		});

	}

	public void checkSDCard() {

		BitmapDrawable drawable = (BitmapDrawable) iv1.getDrawable(); // convert
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

		Toast.makeText(getApplicationContext(), "Try another image!",
				Toast.LENGTH_SHORT).show();

	}

	public void initalise_crystal_buttons() {

		save_img1 = (Button) findViewById(R.id.save_crystal_img);
		reflection2 = (Button) findViewById(R.id.Filter13);
		gray_scale2 = (Button) findViewById(R.id.Filter12);
		neon_filter1 = (Button) findViewById(R.id.Filter11);
		bright_filter1 = (Button) findViewById(R.id.Filter10);
		dark_filter1 = (Button) findViewById(R.id.Filter9);
		undo1 = (Button) findViewById(R.id.Undo_crystal_img);
		round_corner1 = (Button) findViewById(R.id.Filter14);
	}

}
