package com.testcode.musicsite;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MusicListAdapter extends BaseAdapter implements MusicItemView.IActionListener
{
	private static final String TAG = "MusicListAdapter";
	
	Context mContext;
	List<MusicItem> mMusicList = new ArrayList<MusicItem>();
	
	MusicListAdapter(Context context) {
		mContext = context;
	}
	
	public void bindData(List<MusicItem> list) {
		mMusicList = list;
	}

	@Override
	public int getCount() {
		return mMusicList.size();
	}

	@Override
	public MusicItem getItem(int position) {
		return mMusicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Identify whether we need to create a new view
		if (convertView == null || convertView.getId() != R.layout.main_list_item_layout) {
			// Create music item view for list view
			MusicItemView view = new MusicItemView(mContext);
			
			// Create context view, which displays the contexts.
			View contextView = View.inflate(mContext, R.layout.main_list_item_layout, null);
			// Create Initialize helper class.
			MusicItem.ViewHolder holer = MusicItem.ViewHolder.createMainItem(contextView);
			// Initialize the context view and then do some initialize works for list item view.
			MusicItem item = mMusicList.get(position);
			if (item != null) {
				holer.intialize(item);
				
				view.setContextView(contextView);
				view.setPosition(position);
				view.setActionListener(this);
				
				convertView = view;
			}
		}
		
		return convertView;
	}

	private void deleteItem(final int position) {
		if (position != -1) {
			mMusicList.remove(position);
			notifyDataSetChanged();	
		}
	}
	
	class DialogButtonListener implements DialogInterface.OnClickListener {
		
		private int mPosition = -1;
		
		public void setPosition(final int position) {
			mPosition = position;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				deleteItem(mPosition);
			}
			mPosition = -1;
			dialog.dismiss();
		}
	};
	
	
	DialogButtonListener mDialogButtonInterface = new DialogButtonListener();
	
	@Override
	public void doAction(int action, final MusicItemView view) {
		if (action == MusicItemView.IActionListener.ACTION_DELETE_CLICKED) {
			
			if (view != null) {
				Log.v(TAG, "delete button clicked pos=" + view.getPosition());

				mDialogButtonInterface.setPosition(view.getPosition());
				
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("Do you confirm to delete this item")
			           .setCancelable(true)
			           .setNegativeButton("Cancel", mDialogButtonInterface)
			           .setPositiveButton("OK", mDialogButtonInterface);
				
				AlertDialog dlg = builder.create();
				dlg.show();
			}
		}
	}

}
