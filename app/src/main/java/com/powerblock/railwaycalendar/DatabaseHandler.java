package com.powerblock.railwaycalendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "trainTimesDatabase.db";
	private final static int DATABASE_VERSION = 6;
	private final static String TABLE_YEAR_STARTS = "tableOfYearStarts";
	
	public final static String KEY_YEAR = "year";
	public final static String KEY_MONTH = "month";
	public final static String KEY_DAY = "day";
	
	public DatabaseHandler(FragmentActivity context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTable = "CREATE TABLE " + TABLE_YEAR_STARTS + "("
				+ KEY_YEAR + " INTEGER PRIMARY KEY," + KEY_MONTH + " INTEGER," + KEY_DAY + " INTEGER)";
		db.execSQL(createTable);
		ContentValues values = new ContentValues();
		values.put(KEY_YEAR, 2015);
		values.put(KEY_MONTH, 3);
		values.put(KEY_DAY, 28);
		db.insert(TABLE_YEAR_STARTS, null, values);
		values= new ContentValues();
		values.put(KEY_YEAR, 2016);
		values.put(KEY_MONTH, 4);
		values.put(KEY_DAY, 2);
		db.insert(TABLE_YEAR_STARTS, null, values);
		values = new ContentValues();
		values.put(KEY_YEAR,2017);
		values.put(KEY_MONTH,4);
		values.put(KEY_DAY,1);
		db.insert(TABLE_YEAR_STARTS, null, values);
		values = new ContentValues();
		values.put(KEY_YEAR,2018);
		values.put(KEY_MONTH,3);
		values.put(KEY_DAY,31);
		db.insert(TABLE_YEAR_STARTS,null,values);
		values = new ContentValues();
		values.put(KEY_YEAR,2019);
		values.put(KEY_MONTH,3);
		values.put(KEY_DAY,30);
		db.insert(TABLE_YEAR_STARTS,null,values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String upgradeTable = "DROP TABLE IF EXISTS "+ TABLE_YEAR_STARTS;
		db.execSQL(upgradeTable);
		onCreate(db);
	}
	
	public ContentValues getDate(int year){
		SQLiteDatabase db = getReadableDatabase(); 
		
		Cursor cursor = db.query(TABLE_YEAR_STARTS, new String[]{KEY_YEAR, KEY_MONTH, KEY_DAY}, KEY_YEAR+"=?", new String[]{String.valueOf(year)}, null, null, null);
		
		if(cursor.moveToFirst()){
			Log.v("Cursor", "not null");
			int month = cursor.getInt(1);
			int day = cursor.getInt(2);
			Log.v("Cursor", "Month: " + String.valueOf(month) + " Day: " + String.valueOf(day));
			cursor.close();
			ContentValues values = new ContentValues();
			values.put(KEY_MONTH, month);
			values.put(KEY_DAY, day);
			values.put(KEY_YEAR, year);
			db.close();
			return values;
		} else {
			ContentValues values = new ContentValues();
			values.put(KEY_MONTH, -1);
			values.put(KEY_DAY, -1);
			values.put(KEY_YEAR, -1);
			return values;
		}
	}

}

