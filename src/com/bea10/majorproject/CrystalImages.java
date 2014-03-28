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

public class CrystalImages extends Activity {

	ImageView selectedImage;
	private Integer[] mImageIds = { R.drawable.image1, R.drawable.image2,
			R.drawable.image3, R.drawable.image4, R.drawable.image5,
			R.drawable.image6, R.drawable.image7, R.drawable.image8,
			R.drawable.image9, R.drawable.image10, R.drawable.image11 };

	Bitmap bi;
	InputStream is;
	ImageView iv1;
	Button bf, bf2, save_img1;
	int time = (int) System.currentTimeMillis();
	private Bitmap operation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crystal_images);


		// BitmapDrawable abmp = (BitmapDrawable)selectedImage.getDrawable();
		// bi = abmp.getBitmap();

		// InputStream is = getResources().openRawResource(R.drawable.image1);
		// bi = BitmapFactory.decodeStream(is);

		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		selectedImage = (ImageView) findViewById(R.id.crystal_img_imgV);
		gallery.setSpacing(1);
		gallery.setAdapter(new GalleryImageAdapter(this));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// show the selected Image
				selectedImage.setImageResource(mImageIds[position]);
			}
		});
		
		
		
		bf = (Button) findViewById(R.id.Filter9);

		bf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				BitmapDrawable abmp = (BitmapDrawable) selectedImage
						.getDrawable();
				bi = abmp.getBitmap();
				bright(selectedImage);

			}

	
		});
		
		bf2 = (Button) findViewById(R.id.Filter10);
		
		bf2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				BitmapDrawable abmp = (BitmapDrawable) selectedImage
						.getDrawable();
				bi = abmp.getBitmap();
				dark(selectedImage);

			}

	
		});
	
		save_img1 = (Button) findViewById(R.id.save_crystal_img);
		
		save_img1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				checkSDCard();

			}

	
		});
		
	}
	
	
	
	

	public void bright(View view) {
		operation = Bitmap.createBitmap(bi.getWidth(), bi.getHeight(),
				bi.getConfig());

		for (int i = 0; i < bi.getWidth(); i++) {
			for (int j = 0; j < bi.getHeight(); j++) {
				int p = bi.getPixel(i, j);
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
		selectedImage.setImageBitmap(operation);
	}

	public void dark(View view) {
		operation = Bitmap.createBitmap(bi.getWidth(), bi.getHeight(),
				bi.getConfig());

		for (int i = 0; i < bi.getWidth(); i++) {
			for (int j = 0; j < bi.getHeight(); j++) {
				int p = bi.getPixel(i, j);
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

		selectedImage.setImageBitmap(operation);
	}
	
	public void checkSDCard() {

		iv1 = (ImageView) findViewById(R.id.crystal_img_imgV);
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

		Toast.makeText(getApplicationContext(),
				"Try adding an effect to another image!", Toast.LENGTH_SHORT)
				.show();

	}
}
