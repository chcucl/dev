package com.testcode.musicsite;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicItem 
{
	private String mId;
	private String mTitle;
	private String mArtist;
	private String mDuration;
	private Bitmap mThumb;
	
	public MusicItem(String Id, String title, String artist, String duration, Bitmap bmp) {
		mId = Id;
		mTitle = title;
		mArtist = artist;
		mDuration = duration;
		mThumb = bmp;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getArtist() {
		return mArtist;
	}
	
	public String getDuration() {
		return mDuration;
	}
	
	public Bitmap getThumb() {
		return mThumb;
	}
	
	public String toString() {
		return "id: " + mId + " title: " + mTitle + " art: " + mArtist + " duration: " + mDuration + " URL: " + mThumb;
	}
	
	/*
	 * ViewHolder
	 * 
	 * This is used to initialize the context of views, they are:
	 * <1> main_list_item_layout 
	 * <2> main_list_item_slide: this view does not include image view
	 * */
	public static class ViewHolder {
		public ImageView img;
		public TextView  title;
		public TextView  artist;
		public TextView  dura;
		
		protected ViewHolder(View parent) {
			title = (TextView) parent.findViewById(R.id.main_list_item_music_title);
			artist = (TextView) parent.findViewById(R.id.main_list_item_music_artist);
			dura = (TextView) parent.findViewById(R.id.main_list_item_music_duration);
			img = (ImageView) parent.findViewById(R.id.main_list_item_image);
		}
		
		public void intialize(MusicItem item) {
			if (item != null) {
				title.setText(item.getTitle());
				artist.setText(item.getArtist());
				dura.setText(item.getDuration());
				img.setImageBitmap((Bitmap)item.getThumb());
			}
		}
		
		/*
		 * Create ViewHolder instance only for main_list_item_layout, it includes image view
		 * */
		public static ViewHolder createMainItem(View parent) {
			return new ViewHolder(parent);
		}
	}
}
