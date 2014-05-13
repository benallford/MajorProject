package com.bea10.majorproject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/*
 * This class holds all the methods for apply effects to the image whether it be user taken images, crystal images or images from the image library
 */

public class ImageEffects {
	
	public static final double PI = 3.14159d;
	public static final double FULL_CIRCLE_DEGREE = 360d;
	public static final double HALF_CIRCLE_DEGREE = 180d;
	public static final double RANGE = 256d;
//	Matrix matrix = new Matrix();
//	Matrix savedMatrix = new Matrix();
//	PointF startPoint = new PointF();
//	PointF midPoint = new PointF();
//	float oldDist = 1f;
//	static final int NONE = 0;
//	static final int DRAG = 1;
//	static final int ZOOM = 2;
//	int mode = NONE;
	
	/*
	 * This method inverts the pixel colours of a bitmap image, it takes in the bitmap that the user wants changed
	 */
	public static Bitmap invertImageColours(Bitmap src) {
		// create new bitmap with the same settings as source bitmap
		Bitmap new_bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// color info
		int Alpha, Blue, Green, Red;
		int image_pixelColor;
		// get the image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// get image pixels and store in an integer
				image_pixelColor = src.getPixel(x, y);
				// saving alpha channel
				Alpha = Color.alpha(image_pixelColor);
				// inverting byte for each R/G/B channel
				Red = 255 - Color.red(image_pixelColor);
				Blue = 255 - Color.blue(image_pixelColor);
				Green = 255 - Color.green(image_pixelColor);
				
				
				// set new bitmap to new modified colours
				new_bitmap.setPixel(x, y, Color.argb(Alpha, Red, Green, Blue));
			}
		}

		// return final bitmap with changes applied
		return new_bitmap;
	}

	/*
	 * This method changes the pixels in an image to black and white, it takes in a bitmap the user wants to change
	 */
	public static Bitmap blackAndWhite(Bitmap src) {
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

	/*
	 * This method increases and decreases the brightness of the image, it takes in a bitmap to be modified and an integer value representing
	 * how much each colour channel should be changed by
	 */
	
	public static Bitmap increaseAndDecreaseBrightness(Bitmap src, int colourValue) {
		// image size
		int height = src.getHeight();
		int width = src.getWidth();
		// create output bitmap
		Bitmap new_bitmap = Bitmap.createBitmap(width, height, src.getConfig());
		// colour information
		int Alpha, Red, Green, Blue;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel colour
				pixel = src.getPixel(x, y);
				Alpha = Color.alpha(pixel);
				Red = Color.red(pixel);
				Green = Color.green(pixel);
				Blue = Color.blue(pixel);

				// increase/decrease each channel by chaning value based on int passed in 
				Red += colourValue;
				if (Red > 255) {
					Red = 255;
				} else if (Red < 0) {
					Red = 0;
				}
				//same for this 
				Green += colourValue;
				if (Green > 255) {
					Green = 255;
				} else if (Green < 0) {
					Green = 0;
				}
				//and this
				Blue += colourValue;
				if (Blue > 255) {
					Blue = 255;
				} else if (Blue < 0) {
					Blue = 0;
				}

				// apply new pixel colour to output bitmap
				new_bitmap.setPixel(x, y, Color.argb(Alpha, Red, Green, Blue));
			}
		}

		// return new bitmap with changed colour values
		return new_bitmap;
	}

	/*
	 * This method applies a reflection effect to an image and takes in the bitmap which wishes to be changed
	 * 
	 */
	
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

	/*
	 * This method removes the corners from an image and takes for its parameters the source of a bitmap and a float representing
	 * the degree of the curve when rounding the images off
	 */
	
	public static Bitmap roundCornerOfImage(Bitmap src, float round) {
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
	
	/*
	 * This method applies a shading effect to an image, it takes in the bitmap which you wish to change and the colour of the shade
	 * to be applied to the pixels within the image
	 */
	public static Bitmap shadeImage(Bitmap source, int shadeColour) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] imagePixels = new int[width * height];
		// get pixel array from source
		source.getPixels(imagePixels, 0, width, 0, 0, width, height);

		int index = 0;
		// get the pixels in the image
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in matrix
				index = y * width + x;
				//change the pixels at each index to specific colour passed in to method
				imagePixels[index] &= shadeColour;
			}
		}
		// create new bitmap with the new colours
		Bitmap bmOut = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888); //each pixel is stored on 4 bytes ensures better quality image
		bmOut.setPixels(imagePixels, 0, width, 0, 0, width, height);
		return bmOut; //return new bitmap with changed colours
	}

	/*
	 * This method applies a tint effect to an image by emphasising certain colours, it takes in the source of a bitmap and the degree to represent
	 * how much to tint the image by.
	 */
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

	/*
	 * 
	 * This method was going to be used for creating the silhouette effect by replacing the colours of an image so it could achieve
	 * the silhouette like effect.
	 * 
	 */
	
	public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
     // Source image size 
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);
  
        for(int x = 0; x < pixels.length; ++x) {
            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
        }
     // create result bitmap output 
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);
  
        return result;
	}
	
/*
 * 
 * This method would have been for zooming in on the image view but this functionality was removed in an earlier version
 * 
 */
	
//	public void zoom(){
//	Menu.iv.setOnTouchListener(new View.OnTouchListener() {
//		@Override
//		public boolean onTouch(View v, MotionEvent event) {
//			ImageView view = (ImageView) v;
//			System.out.println("matrix=" + savedMatrix.toString());
//			switch (event.getAction() & MotionEvent.ACTION_MASK) {
//			case MotionEvent.ACTION_DOWN:
//				savedMatrix.set(matrix);
//				startPoint.set(event.getX(), event.getY());
//				mode = DRAG;
//				break;
//			case MotionEvent.ACTION_POINTER_DOWN:
//				oldDist = spacing(event);
//				if (oldDist > 10f) {
//					savedMatrix.set(matrix);
//					midPoint(midPoint, event);
//					mode = ZOOM;
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//			case MotionEvent.ACTION_POINTER_UP:
//				mode = NONE;
//				break;
//			case MotionEvent.ACTION_MOVE:
//				if (mode == DRAG) {
//					matrix.set(savedMatrix);
//					matrix.postTranslate(event.getX() - startPoint.x,
//							event.getY() - startPoint.y);
//				} else if (mode == ZOOM) {
//					float newDist = spacing(event);
//					if (newDist > 10f) {
//						matrix.set(savedMatrix);
//						float scale = newDist / oldDist;
//						matrix.postScale(scale, scale, midPoint.x,
//								midPoint.y);
//					}
//				}
//				break;
//			}
//			view.setImageMatrix(matrix);
//			return true;
//		}
//
//		@SuppressLint("FloatMath")
//		private float spacing(MotionEvent event) {
//			float x = event.getX(0) - event.getX(1);
//			float y = event.getY(0) - event.getY(1);
//			return FloatMath.sqrt(x * x + y * y);
//		}
//
//		private void midPoint(PointF point, MotionEvent event) {
//			float x = event.getX(0) + event.getX(1);
//			float y = event.getY(0) + event.getY(1);
//			point.set(x / 2, y / 2);
//		}
//	});
}


