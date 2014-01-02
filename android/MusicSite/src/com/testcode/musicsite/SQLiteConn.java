package com.testcode.musicsite;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteConn extends SQLiteOpenHelper
{
	private static final int VERSION = 1;
	private static final String TAG = "SQLiteConn";
	
	private static final String mMusicDB = "music_db.db";
	private static final String mMusicTable = "music_table";
	
	private static final String mMusicTableId       = "id";
	private static final String mMusicTableTitle    = "title";
	private static final String mMusicTableArtist   = "artist";
	private static final String mMusicTableDuration = "duration";
	private static final String mMusicTableThumbURL = "thumbURL";
	
	private static final String mMusicTableNull = "mtnull";

	public SQLiteConn(Context context) {
		super(context, mMusicDB, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * Called when the database is created for the first time.
		 * 
		 *  public String Id;
			public String Title;
			public String Artist;
			public String Duration;
			public String ThumbURL;
		 * */
		Log.v(TAG, "onCreate: " + db.toString());
		
		final String createSQL = "CREATE TABLE "
								+ mMusicTable + "( "
				                + mMusicTableId + " int, "
								+ mMusicTableTitle + " varchar(30), "
				                + mMusicTableArtist + " varchar(30), "
								+ mMusicTableDuration + " varchar(10), "
				                + mMusicTableThumbURL + " varchar(80) "
								+ " )";
		
		Log.v(TAG, "create table: " + createSQL);
		
		try {
			db.execSQL(createSQL);
		}
		catch (SQLException e) {
			Log.e(TAG, "create table expr: " + e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	
		Log.v(TAG, "onOpen: " + db.toString());
	}
	
	public static SQLiteConn Create(Context context) {
		return new SQLiteConn(context);
	}
	
	public void StoreMusicItem(final MusicItem musicItem)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(mMusicTableId      , musicItem.getId());
		values.put(mMusicTableTitle   , musicItem.getTitle());
		values.put(mMusicTableArtist  , musicItem.getArtist());
		values.put(mMusicTableDuration, musicItem.getDuration());
		values.put(mMusicTableThumbURL, musicItem.getThumb().toString());
		
		final long ret = db.insert(mMusicTable, mMusicTableNull, values);
		if (ret == -1) {
			Log.e(TAG, "insert to table failed->" + musicItem.toString());
		}
		
		db.close();
	}
	
	public void StoreMusicItems(final List<MusicItem> listItem) 
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ListIterator<MusicItem> listItr = listItem.listIterator();
		for (; listItr.hasNext(); ) 
		{
			final MusicItem musicItem = listItr.next();
			
			ContentValues values = new ContentValues();
			values.put(mMusicTableId      , musicItem.getId());
			values.put(mMusicTableTitle   , musicItem.getTitle());
			values.put(mMusicTableArtist  , musicItem.getArtist());
			values.put(mMusicTableDuration, musicItem.getDuration());
			values.put(mMusicTableThumbURL, musicItem.getThumb().toString());
			
			final long ret = db.insert(mMusicTable, mMusicTableNull, values);
			if (ret == -1) {
				Log.e(TAG, "insert to table failed->" + musicItem.toString());
			}
		}
		
		db.close();
	}
	
	public List<MusicItem> GetMusicItems() 
	{
		List<MusicItem> list = new ArrayList<MusicItem>();
		
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			Cursor cursor = db.query(mMusicTable, null, null, null, null, null, null);
			cursor.moveToFirst();
			for (; cursor.isAfterLast(); cursor.moveToNext()) {
				
				MusicItem item = new MusicItem(cursor.getString(0),  /* Id */
											   cursor.getString(1),  /* Title */
											   cursor.getString(2),  /* Artist */
											   cursor.getString(3),  /* Duration */
											   null);
				
				list.add(item);
			}
			
			cursor.close();
		}
		catch (SQLiteException e) {
			
		}
		finally {
			if (db != null) {
				db.close();
			}
		}
		
		return list;
	}
}
