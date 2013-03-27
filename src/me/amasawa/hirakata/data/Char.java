package me.amasawa.hirakata.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Char implements Parcelable {
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_H = "hiragana";
	public static final String COLUMN_K = "katakana";
	public static final String COLUMN_R = "romaji";
	public static final String COLUMN_P = "polivanov";
	public static final String COLUMN_TYPE = "type";
	public static final int TYPE_BIG = 0;
	public static final int TYPE_SMALL = 1;
	private String id;
	private String hiragana;
	private String katakana;
	private String romaji;
	private String polivanov;
	private String type;
	public Char(String id, String hiragana, String katakana, String romaji, String polivanov, String type) {
		this.id = id;
		this.hiragana = hiragana;
		this.katakana = katakana;
		this.romaji = romaji;
		this.polivanov = polivanov;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public String getHiragana() {
		return hiragana;
	}
	public String getKatakana() {
		return katakana;
	}
	public String getRomaji() {
		return romaji;
	}
	public String getPolivanov() {
		return polivanov;
	}
	public String getType() {
		return type;
	}
	public Char (Parcel in) {
		String data[] = new String[6];
		in.readStringArray(data);
		this.id = data[0];
		this.hiragana = data[1];
		this.katakana = data[2];
		this.romaji = data[3];
		this.polivanov = data[4];
		this.type = data[5];
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{
			this.id,
			this.hiragana,
			this.katakana,
			this.romaji,
			this.polivanov,
			this.type
		});
	}
	public static final Creator CREATOR = new Creator() {
		public Char createFromParcel(Parcel in) {
			return new Char(in);
		}
		public Char[] newArray(int size) {
			return new Char[size];
		}
	};
}