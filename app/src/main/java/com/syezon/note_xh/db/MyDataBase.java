package com.syezon.note_xh.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 老便签的数据库
 */
public class MyDataBase {
	MySQLiteOpenHelper mHelper;
	SQLiteDatabase db;

	public MySQLiteOpenHelper getmHelper() {
		return mHelper;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public MyDataBase(Context context) {
		this.mHelper = new MySQLiteOpenHelper(context, "note.db", null, 1);
		db = mHelper.getReadableDatabase();
	}

	/**
	 * 获取老便签数据库中的数据
	 */
	public List<Note> getAllNoteList() {
		Cursor cursor = db.rawQuery("select * from notes order by _id", null);
		List<Note> list = new ArrayList<>();
		if(cursor != null && cursor.moveToFirst()){
			do{
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				String date = cursor.getString(cursor.getColumnIndex("date"));
				String week = cursor.getString(cursor.getColumnIndex("week"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				Note note = new Note();
				note.setId(_id);
				note.setDate(date);
				note.setWeek(week);
				note.setTime(time);
				note.setContent(content);
				list.add(note);
			}while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

}
