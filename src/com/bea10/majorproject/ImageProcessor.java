package com.bea10.majorproject;

import android.graphics.Bitmap;

public class ImageProcessor {
    Bitmap mImage;
    boolean mIsError = false;
 
    public ImageProcessor(final Bitmap image) {
        mImage = image.copy(image.getConfig(), image.isMutable());
        if(mImage == null) {
            mIsError = true;
        }
    }
 
    public boolean isError() {
        return mIsError;
    }
 
    public void setImage(final Bitmap image) {
        mImage = image.copy(image.getConfig(), image.isMutable());
        if(mImage == null) {
            mIsError = true;
        } else {
            mIsError = false;
        }
    }
 
    public Bitmap getImage() {
        if(mImage == null){
            return null;
        }
        return mImage.copy(mImage.getConfig(), mImage.isMutable());
    }
 
    public void free() {
        if(mImage != null && !mImage.isRecycled()) {
            mImage.recycle();
            mImage = null;
        }
    }
 
    public Bitmap replaceColor(int fromColor, int targetColor) {
        if(mImage == null) {
            return null;
        }
 
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        int[] pixels = new int[width * height];
        mImage.getPixels(pixels, 0, width, 0, 0, width, height);
 
        for(int x = 0; x < pixels.length; ++x) {
            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
        }
 
        Bitmap newImage = Bitmap.createBitmap(width, height, mImage.getConfig());
        newImage.setPixels(pixels, 0, width, 0, 0, width, height);
 
        return newImage;
    }
}