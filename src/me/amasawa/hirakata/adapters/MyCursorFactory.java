package me.amasawa.hirakata.adapters;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;
import me.amasawa.hirakata.Constants;

public class MyCursorFactory implements SQLiteDatabase.CursorFactory{
	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		if (Constants.DEBUG)
			Log.d(Constants.TAG, query.toString());
		return new SQLiteCursor(db, masterQuery, editTable, query);
	}
}
