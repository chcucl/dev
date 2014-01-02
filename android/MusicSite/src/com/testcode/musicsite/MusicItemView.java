package com.testcode.musicsite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

@SuppressLint("ViewConstructor") // ???

public class MusicItemView extends LinearLayout
{
	private static final String TAG = "MusicItemLayout";
	
	private Context mContext;
	private RelativeLayout mContextView;
	private int mPosition = -1;
	
	private Scroller mScroller;
	private int mHotWidth = 120;  // the width of rectangle which user could trigger scrolling

	
	/*
	 * Interface for translate event to listener.
	 * */
	public interface IActionListener
	{	
		public static final int ACTION_DELETE_CLICKED = 1;  // delete button clicked event
		
		public abstract void doAction(int action, final MusicItemView view);
	}

	IActionListener mActionListener = null;
	DeleteButtonListener mDeleteButtonListener = new DeleteButtonListener();
	
	public MusicItemView(Context context) {
		super(context);
		
		mContext = getContext();
		initalize();
	}
	
	private void initalize() {
		mScroller = new Scroller(mContext);
		
		View.inflate(mContext, R.layout.main_list_item_slide, this);
		mContextView = (RelativeLayout)this.findViewById(R.id.main_list_item_context);
		mHotWidth = Math.round(
				TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, mHotWidth, getResources().getDisplayMetrics()));
		
		Button button = (Button)this.findViewById(R.id.main_list_item_delete_button);
		if (button != null) {
			button.setOnClickListener(mDeleteButtonListener);
		}
	}
	
	public void setContextView(View view) {
		mContextView.addView(view);
	}
	
	public void setPosition(final int position) {
		mPosition = position;
	}
	
	public int getPosition() {
		return mPosition;
	}
	
	public void setActionListener(IActionListener actionListener) {
		mActionListener = actionListener;
	}
	
	public void startScroll(final int dstX, final int dstY) {
		final int scrollX = this.getScrollX();
		int delta = dstX - scrollX;
		mScroller.startScroll(scrollX, 0, delta, 0);
		invalidate();
	}
	
	public void abortScroll() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
//			mScrollState = SCROLL_START;
		} 
		else {
			/*
			 * If the current list item have been scrolled to left side, make it return home.
			 * */
			int scrollx = this.getScrollX();
			if (scrollx > 0) {
				mScroller.startScroll(scrollx, getScrollY(), -scrollx, 0, scrollx*3);
				Log.v(TAG, "go back home: scrollx=" + scrollx);
			}
			else {
				Log.e(TAG, "scroll should be always positive value!!");
			}
		}
	}
	
	private int mLastX = 0;
	private int mLastY = 0;
	
	public void onRequiredEvent(MotionEvent event) {
		final int x = (int)event.getX();
		final int y = (int)event.getY();
		final int scrollX = getScrollX();  // here, scrollx should be last scroll position.
		
//		Log.v(TAG, "scroll (x:" + x + " y:" + y + ") scrollX=" + scrollX);
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// If current scrolling has not finished, abort it.
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
		}
		break;
		
		case MotionEvent.ACTION_MOVE: {
			/*
			 * The touch is moving, the selected view should be moving along with touch.
			 * It means that where the touch is, where the view is.
			 * */
			
			int deltaX = x - mLastX;
			int deltaY = y - mLastY;
			if (Math.abs(deltaX) < Math.abs(deltaY) * 2) {
				break;
			}
			
//			mScrollState = SCROLL_MOVING;

			/* calculate the new position of scroll:
			 *  last position of scroll - delta (the distance of moving)
			 *  
			 *  Why using minus:
			 *  Because the scrolling is to left side, so the delta should be negative value.
			 *  However, the scroll value should increase to larger value. So, you know, negative 
			 *  and negative, we get positive value.
			 **/
			int newScrollX = scrollX - deltaX;
			if (deltaX != 0) {
				newScrollX = (newScrollX < 0) ? 0 : newScrollX;
				newScrollX = (newScrollX > mHotWidth) ? mHotWidth : newScrollX;
				
				// set new position of scroll.
				this.scrollTo(newScrollX, this.getScrollY());
			}
		}
		break;
		
		case MotionEvent.ACTION_UP: {
						
//			mScrollState = SCROLL_MOVING;
			
			/*
			 * The touch is up, here needs to determine whether the view moves to left side 
			 * or return to original state.
			 * 
			 * Here calculate the distance between the current position of view and the destination
			 * position of view. If the distance is larger than 0.75 * mHotWidth, scroll the view 
			 * to left side, otherwise, return to original state.
			 * 
			 * */
			int delta = 0;
			int timedelay = 0;
			int scrollx = getScrollX();
			if (scrollX - mHotWidth*0.75 > 0) {
				delta = mHotWidth - scrollx;
				timedelay = Math.abs(delta)*3;
			} 
			else {
				delta = -scrollx;
				timedelay = Math.abs(delta)*3;
			}
			
			Log.v(TAG, "ACTION_UP: " + delta);

			mScroller.startScroll(scrollx, getScrollY(), delta, 0, timedelay);
		}
		break;
		
		default: break;
		}
		
		mLastX = x;
		mLastY = y;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			this.postInvalidate();
		}
	}
	
	
	private class DeleteButtonListener implements OnClickListener 
	{
		@Override
		public void onClick(View view) {
			if (view instanceof Button 
					&& view.getId() == R.id.main_list_item_delete_button) {
				if (mActionListener != null) {
					mActionListener.doAction(IActionListener.ACTION_DELETE_CLICKED, MusicItemView.this);
				}
			}
		}
		
	}
}
