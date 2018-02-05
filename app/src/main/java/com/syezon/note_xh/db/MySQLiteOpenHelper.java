package com.syezon.note_xh.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	public MySQLiteOpenHelper(Context context, String name, CursorFactory factory,
							  int version) {
		super(context, name, factory, version);
	}

	private static final String CREATE_TABLE_NOTAS = "create table notes(_id integer primary key autoincrement,date varchar(100) not null,week varchar(100) not null,time varchar(100) not null,content varchar(100) not null)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_NOTAS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}


}
