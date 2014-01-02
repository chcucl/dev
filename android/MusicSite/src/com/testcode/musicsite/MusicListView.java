package com.testcode.musicsite;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class MusicListView extends ListView
{
	private static final String TAG = "MusicListView";
	
	private MusicItemView mSelected = null;

	public MusicListView(Context context) { 
		super(context);
	}

	public MusicListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	public MusicListView(Context context, AttributeSet attrs, int defStyle) {
	   super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// a new item selected
			final int x = (int) ev.getX();
			final int y = (int) ev.getY();
			final int position = this.pointToPosition(x, y);
			
			Log.v(TAG, "onTouchEvent position = " + position);
			
			if (position != INVALID_POSITION) {
				if (mSelected != null) {
					mSelected.abortScroll();
					mSelected = null;
				}
				mSelected = (MusicItemView)this.getChildAt(position);
			}
		}
		
		if (mSelected != null) {
			mSelected.onRequiredEvent(ev);
		}
		
		return super.onTouchEvent(ev);
	}
}
