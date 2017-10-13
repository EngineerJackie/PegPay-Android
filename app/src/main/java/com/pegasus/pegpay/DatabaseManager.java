/**
 * @file DatabaseManager.java
 * @author Zed Jasper Onono
 * @date May 20, 2013 
 * Copyright (c) 2013 Kola Studios. All Rights Reserved 
 */

package com.pegasus.pegpay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "pegpay.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String cacheQuery = "CREATE TABLE IF NOT EXISTS " + CacheManager.TABLE_NAME
				+ " (" + CacheManager.COLUMN_ID + " INTEGER PRIMARY KEY, "
				+ CacheManager.COLUMN_URL + " TEXT, "
				+ CacheManager.COLUMN_CONTENT + " TEXT)";
		db.execSQL(cacheQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Utils.log("onUpgrade");
		
		// Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + CacheManager.TABLE_NAME);
        
        // Create tables again
        onCreate(db);
	}
}
