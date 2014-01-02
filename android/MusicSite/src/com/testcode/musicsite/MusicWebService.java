package com.testcode.musicsite;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


/*
 * WARNING: the main activity should use some synchronize method to make sure
 * that thread in service has been starting before posting message to it.
 * */

public class MusicWebService extends Service 
{
	private static final String TAG = "MusicWebService";
	
	private Handler mHandler;
	private Thread  mThread;
	private final LoadBinder mBinder = new LoadBinder();


	/**
	 * Class for clients to access
	 * */
	public class LoadBinder extends Binder {
		MusicWebService getService() {
			return MusicWebService.this;
		}
		
		void requestWebsiteXml(Handler replyHandler) {
			if (mHandler != null) {
				try {
					mHandler.obtainMessage(MusicMsg.MSG_REQUEST_XML, replyHandler).sendToTarget();
				}
				catch (NullPointerException e) {
					Log.e(TAG, "thread has not started, put it to cache");
				}
			}
		}
	}
	
	 static class ThreadHandler extends Handler {
		 
		 private final WeakReference<MusicWebService> mService;
		 
		 public ThreadHandler(MusicWebService service) {
			 mService = new WeakReference<MusicWebService>(service);
		 }

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MusicMsg.MSG_REQUEST_XML) {
				
				Log.v(TAG, "handleMessage -> MSG_REQUEST_XML");
				
				MusicWebService service = mService.get();
				if (service != null) {
					service.connectWebsite((Handler)msg.obj);
				}
			}
			else {
				super.handleMessage(msg);
			}
		}	
	}

	/**
	 * Thread is used to load XML from music web site.
	 * */
	private class LoaderThread extends Thread 
	{	
		@Override
		public void run() {
			Log.v(TAG, "LoaderThread -> run");
			
			Looper.prepare();  // create message queue
			mHandler = new ThreadHandler(MusicWebService.this);
			Looper.loop();
		}
	}
	
	private void connectWebsite(Handler replyHandler) {
		try {
			
			MusicListLoader listLoader = new MusicListLoader(this.getApplicationContext());
			List<MusicItem> list = listLoader.loadMusicList();
			
			if (replyHandler != null) {
				replyHandler.obtainMessage(MusicMsg.MSG_REPLY_XML, list).sendToTarget();
			}
		}
		catch (Exception e) {
			Log.e(TAG, "connectWebsite expr: " + e.getMessage());
		}
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.v(TAG, "create service");
		
		mThread = new LoaderThread();
		mThread.start();
	}

	@Override
	public void onDestroy() {
		
		Log.v(TAG, "destroy service");
		mThread.interrupt();
		
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

}
