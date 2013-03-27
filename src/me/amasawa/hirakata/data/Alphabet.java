package me.amasawa.hirakata.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.Constants;
import me.amasawa.hirakata.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class Alphabet {
	BaseApplication application;
	DBHelper dbHelper;
	SQLiteDatabase sqLiteDatabase;
	ArrayList<Integer> gen;

	public Alphabet (BaseApplication application) {
		this.application = application;
		this.dbHelper = application.getDBHelper();
		this.gen = new ArrayList<Integer>();
		sqLiteDatabase = dbHelper.getReadableDatabase();
	}
	public Char[] getChoiseChars() {
		List<String> listTypes = getTypesFromParams();
		Char[] choiseChars = new Char[5];
		for (int i=0;i<5;i++)
			choiseChars[i] = getRandomChar(listTypes);
		gen.clear();
		return choiseChars;
	}
	private Char getRandomChar(List<String> listTypes) {
		String selection = getSelectionFromTypes(listTypes.size());
		String[] types = listTypes.toArray(new String[listTypes.size()]);
		Cursor c = sqLiteDatabase.query("alphabet", null, selection, types, null, null, null);
		int max = c.getCount();
		int rand;
		while (true) {
			rand = (int)(Math.random() * max);
			if (!gen.contains(rand)) {
				gen.add(rand);
				break;
			}
		}
		c.moveToPosition(rand);
		return makeCharFromCursor(c);
	}

	private Char makeCharFromCursor (Cursor c) {
		Char aChar;
		if (Constants.DEBUG) Log.d(Constants.TAG, "Count: " + c.getCount() + ". Current:" + c.getPosition());
		aChar = new Char(
				String.valueOf(c.getInt(c.getColumnIndex(Char.COLUMN_ID))),
				c.getString(c.getColumnIndex(Char.COLUMN_H)),
				c.getString(c.getColumnIndex(Char.COLUMN_K)),
				c.getString(c.getColumnIndex(Char.COLUMN_R)),
				c.getString(c.getColumnIndex(Char.COLUMN_P)),
				c.getString(c.getColumnIndex(Char.COLUMN_TYPE))
		);
		return aChar;
	}
	private String getSelectionFromTypes(int typesSize) {
		String selection = "";
		for (int i=0;i<typesSize;i++) {
			if (i==0) {
				selection+="type=?";
				continue;
			}
			selection+=" OR type=?";
		}
		return selection;
	}

	private List<String> getTypesFromParams() {
		SharedPreferences prefs = application.getSharedPreferences("hirakata", Context.MODE_PRIVATE);
		List<String> types = new ArrayList<String>();
		for (String s: Constants.TYPES) {
			if (prefs.getBoolean(s, true))
				types.add(s);
		}
		return types;
	}
}