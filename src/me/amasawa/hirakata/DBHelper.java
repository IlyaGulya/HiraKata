package me.amasawa.hirakata;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import me.amasawa.hirakata.adapters.MyCursorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
	private static final String dbName = "hirakata";
	private static final String[] abName = {"hirakata.txt"};
	private Context context;

	public DBHelper(Context context) {
		super(context, dbName, new MyCursorFactory(), 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(
			"create table alphabet ("+
			"id integer primary key autoincrement,"+
			" hiragana text, katakana text, romaji text, polivanov text, type text);"+
			"create table errors ("+
			"id integer primary key autoincrement,"+
			"hiragana numeric,"+
			"katakana numeric);"
		);
		fillTable("alphabet", sqLiteDatabase);
		if (Constants.DEBUG) Log.d(Constants.TAG, "onCreate()");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

	}

	private void fillTable(String tableName, SQLiteDatabase sqLiteDatabase) {
		ContentValues cv;
		if (Constants.DEBUG) Log.d(Constants.TAG, "sqlitedatabase!=null:" + String.valueOf(sqLiteDatabase!=null));
		if (sqLiteDatabase!=null) {
			for (String str:abName) {
				ArrayList<String> data = getData(str);
				if (Constants.DEBUG) Log.d(Constants.TAG, "data=" + String.valueOf(data!=null));
				for (String cLine:data) {
					String[] cLineData = cLine.split(" ");
					if (Constants.DEBUG) Log.d(Constants.TAG, "[" + cLineData.length + "]cLine=" + cLine);
					if (cLineData.length==6) {
						cv = new ContentValues();
						cv.put("hiragana", cLineData[1]);
						cv.put("katakana", cLineData[2]);
						cv.put("romaji", cLineData[3]);
						cv.put("polivanov", cLineData[4]);
						cv.put("type", cLineData[5]);
						long rowsInserted = sqLiteDatabase.insert(tableName, null, cv);
						if (Constants.DEBUG) Log.d(Constants.TAG, "Rows inserted: " + rowsInserted);
					}
				}
			}
		}
	}

	private ArrayList<String> getData(String fileName) {
		InputStream stream = null;
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			stream = context.getAssets().open(fileName);
		} catch (IOException e) {
			Log.e("amasawa", e.getMessage());
		}
		InputStreamReader sr = new InputStreamReader(stream);
		BufferedReader bufferedReader = new BufferedReader(sr);
		String data;
		try{
			while ((data=bufferedReader.readLine())!=null) {
				arrayList.add(data);
			}
		} catch (IOException e) {
			Log.e("amasawa", e.getMessage());
		}
		return arrayList;
	}
}
