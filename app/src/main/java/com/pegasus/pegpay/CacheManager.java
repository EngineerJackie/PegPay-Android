/**
 * @file CacheManager.java
 * @author Zed Jasper Onono
 * @date May 23, 2013 
 * Copyright (c) 2013 Kola Studios. All Rights Reserved 
 */

package com.pegasus.pegpay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheManager {
	public static final String TABLE_NAME = "cache";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_CONTENT = "content";

	public static void saveDbCache(String url, String content, Context cxt) {
		SQLiteDatabase db = new DatabaseManager(cxt).getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_CONTENT, content);

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_URL + " = '" + md5(url) + "'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			db.update(TABLE_NAME, values, COLUMN_URL + " = ?", new String[] { md5(url) });
		} else {
			values.put(COLUMN_URL, md5(url));
			db.insert(TABLE_NAME, null, values);
		}

		cursor.close();
		db.close();
	}

	public static String getDbCache(String url, Context cxt) {
		SQLiteDatabase db = new DatabaseManager(cxt).getReadableDatabase();
		String cache = null;
		String query = "SELECT " + COLUMN_CONTENT + " FROM " + TABLE_NAME + " WHERE " + COLUMN_URL + " = '" + md5(url) + "'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			cache = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return cache;
	}
	
	public static String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}