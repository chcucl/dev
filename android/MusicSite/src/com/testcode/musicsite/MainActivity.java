package com.testcode.musicsite;

import java.lang.ref.WeakReference;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity 
{
	private static final String TAG = "MainActivity";
	
	MusicListView mMusicListView;
    MusicListAdapter mListAdapter;
    
    ProgressDialog mProgress;  // display the progress dialog while loading music list items data.
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMusicListView = (MusicListView)findViewById(R.id.main_list_view);
        mListAdapter = new MusicListAdapter(this);
        mMusicListView.setAdapter(mListAdapter);
        
        doBindService();
        
        // start process dialog 
        mProgress = new ProgressDialog(this);
        String progressMessage = "Loading.";
        mProgress.setMessage(progressMessage);
        mProgress.setCancelable(false);
        mProgress.show();
    }


    @Override
	protected void onDestroy() {   	
    	doUnbindService();
 
		super.onDestroy();
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	/**********************************************************************************
	 * 
	 * Define and create service
	 * 
	 *********************************************************************************/
    
    private MusicWebService.LoadBinder mSvrBinder;
    
    private ServiceConnection mSvrConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mSvrBinder = (MusicWebService.LoadBinder)service;
			
			/* WARNING:
			 * Must call doInitMusicList in here, because after call bindService, this 
			 * callback may not be dispatch immediately. Assuming that it maybe asynchronous.
			 * */
			doInitMusicList();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mSvrBinder = null;
		}
    	
    };
    
    /**
     * Create and bind service
     * */
    private void doBindService() {
    	
    	Intent intent = new Intent(this, MusicWebService.class);
    	if (!bindService(intent, mSvrConn, Context.BIND_AUTO_CREATE)) {
    		Log.e(TAG, "bind service failed");
    	}
    }
    
    
    private void doUnbindService() {
    	if (mSvrConn != null) {
    		unbindService(mSvrConn);
    	}
    }
    
    /**********************************************************************************
	 * 
	 * Define and create local handler process.
	 * 
	 * StackOverflow
	 * 
	 * Disable Warning
	 * 
	 * URL: "http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler"
	 * 
	 * If IncomingHandler class is not static, it will have a reference to your Service object.
	 * Handler objects for the same thread all share a common Looper object, which they post  
	 * messages to and read from. As messages contain target Handler, as long as there are messages  
	 * with target handler in the message queue, the handler cannot be garbage collected. If handler 
	 * is not static, your Service or Activity cannot be garbage collected, even after being destroyed.
	 * 
	 * 
	 *********************************************************************************/
    
     static class ImcomningHandler extends Handler {

    	 private final WeakReference<MainActivity> mMainActivity;
    	 
    	 public ImcomningHandler(MainActivity mainActivity) {
    		 mMainActivity = new WeakReference<MainActivity>(mainActivity); 
    	 }
    	 
    	 @Override
 		 public void handleMessage(Message msg) {
    		 if (msg.what == MusicMsg.MSG_REPLY_XML) {
  				Log.v(TAG, "Recv reply xml");
  				@SuppressWarnings("unchecked")
  				List<MusicItem> list = (List<MusicItem>)msg.obj;
  				
  				MainActivity mainActivity = mMainActivity.get();
  				if (mainActivity != null) {
  					mainActivity.handleMusicList(list);
  				}
  			}
  			else {
  				super.handleMessage(msg);
  			}
		 }
    };
    
    private Handler mHandler = new ImcomningHandler(this);
   
    
    /**
     * Initialize the music list, send request to service.
     * */
    private void doInitMusicList() {
    	Log.v(TAG, "doInitMusicList requesWebsite XML");
    	
    	if (mSvrBinder != null) {
    		mSvrBinder.requestWebsiteXml(mHandler);
    	}
    }
    
    public void handleMusicList(List<MusicItem> list) {  
    	
    	mListAdapter.bindData(list);
    	mListAdapter.notifyDataSetChanged();
    	
    	if (mProgress != null) {
    		mProgress.dismiss();
    		mProgress = null;
    	}
    }
}
