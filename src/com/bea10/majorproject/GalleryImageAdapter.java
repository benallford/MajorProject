package com.bea10.majorproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
/* 
 * 
 * This class allows the use of the gallery and gives access to methods for getting the length of the array which contains the images,
 *  it allows us to get them item at a certain position of the array and allows us to get the item id of an image in the array.
 *  
 *  The items of Gallery are populated from an Adapter, similar to ListView, in which ListView items are populated from an Adapter.
 *  
 *  
 *  
 */
public class GalleryImageAdapter extends BaseAdapter {
	private Context mContext;

	private Integer[] ImageIds = { R.drawable.image1, R.drawable.image2,
			R.drawable.image3, R.drawable.image4, R.drawable.image5,
			R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11 }; //array containing crystal images

	 public GalleryImageAdapter(Context context) 
	    {
	        mContext = context;
	    }

	    public int getCount() {
	        return ImageIds.length;
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }


	    
	    public View getView(int index, View view, ViewGroup viewGroup) 
	    {
	     
	        ImageView i = new ImageView(mContext);

	        i.setImageResource(ImageIds[index]);
	        i.setLayoutParams(new Gallery.LayoutParams(200, 200));
	    
	        i.setScaleType(ImageView.ScaleType.FIT_XY);

	        return i;
	    }
	}

