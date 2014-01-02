package com.testcode.musicsite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
//import java.util.HashSet;
//import java.util.Set;
//
//import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.os.Environment;
import android.util.Log;


/*
 * MusicImageCache: TBD...
 * */
public class MusicImageCache 
{
	private static final String TAG = "MusicImgCache";
	
	private Context mContext;
	private String mCacheDir;
//	private Set<String> mFiles = new HashSet<String>();
	
	public MusicImageCache(Context context) {
		mContext = context;
		initialize();
	}
	
	/*
	 * Enumerate files in cache folder.
	 * */
	public void initialize() {
//		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//			
//			/*
//			 * If create dir in external storage, must set permission in AndroidManifest.xml
//			 * 
//			 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
//			 * */
//			Log.v(TAG, "create dir in external storage");
//			
//			mCacheDir = Environment.getExternalStorageDirectory() + "/MusicSite/";
//		}
//		else {
//			mCacheDir = mContext.getCacheDir().getPath() + "/";
//		}
		
		mCacheDir = mContext.getCacheDir().getPath() + "/";
	}
	
	public void saveImageToFile(String URL, byte[] bytes, int len) {
		if (mCacheDir.length() == 0) {
			return;
		}
		if (bytes == null || len == 0) {
			return;
		}
		
		Log.v(TAG, "save image to file->" + URL);
		
		final String imgPath = mCacheDir + MD5.getMD5(URL.getBytes());
		File imgf = new File(imgPath);
		FileOutputStream foutput = null;
		try {
			
			if (imgf.exists()) {
				imgf.delete();
			}
			
			if (imgf.createNewFile()) {
				foutput = new FileOutputStream(imgf);
				
				foutput.write(bytes, 0, len);
			}
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "saveImageToFile->file not found->" + e.getMessage());
		}
		catch (IOException e) {
			Log.e(TAG, "saveImageToFile->file create file failed->" + e.getMessage());
		}
		finally {
			if (foutput != null) {
				try {
					foutput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isExist(String URL) {
		return false;
	}
	
	public Bitmap loadImageFromFile(String URL) {
		if (mCacheDir.length() == 0) {
			return null;
		}
			
		Bitmap bmp = null;
		File imgf = null;
		try {
			/*
			 * If relevant file exists, load it from file.
			 * */
			final String imgPath = mCacheDir + MD5.getMD5(URL.getBytes());
			imgf = new File(imgPath);
			if (imgf.exists()) {
				Log.v(TAG, "load image from file->" + URL);
				
				FileInputStream finput = new FileInputStream(imgf);
				bmp = BitmapFactory.decodeStream(finput);
			}	
		}
		catch (FileNotFoundException e) {
			Log.e(TAG, "loadImageFromFile->file not found->" + e.getMessage());
		}
		finally {
		}
		
		return bmp;
	}
}
