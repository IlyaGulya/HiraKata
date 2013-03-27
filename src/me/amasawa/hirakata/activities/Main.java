package me.amasawa.hirakata.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import me.amasawa.hirakata.Constants;
import me.amasawa.hirakata.R;
import me.amasawa.hirakata.adapters.MyPageAdapter;
import me.amasawa.hirakata.data.Char;
import me.amasawa.hirakata.handlers.CharsHandler;
import me.amasawa.hirakata.interfaces.ServiceCallbackListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends BaseActivity implements ServiceCallbackListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
	private TextView tvMain, tvCount;
	private CheckBox cbVowels, cbBase, cbMarks, cbDoubles;
	private Spinner typeMainSpinner, typeButtonsSpinner, numButtonsSpinner;
	private Button btnHint, btnResetSettings, btnResetStats;
	private LinearLayout llButtons;
	private ColorStateList oldColors, oldTextColors;
	private View main, prefs;
	private HashMap<Integer, Integer> numsMap;
	private SharedPreferences sp;
	private SharedPreferences.Editor ed;
	private Char[] currentChars;
	private int numButtons, currentMainId=0, allCount=0, goodCount=0, spinnerFlag = 0;
	private boolean prevHint=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pages = new ArrayList<View>();
		main = inflater.inflate(R.layout.main, null);
		prefs = inflater.inflate(R.layout.preferences, null);
		pages.add(main);
		pages.add(prefs);
		MyPageAdapter pageAdapter = new MyPageAdapter(pages);
		ViewPager viewPager = new ViewPager(this);
		viewPager.setAdapter(pageAdapter);
		viewPager.setCurrentItem(0);
		setContentView(viewPager);
		init(savedInstanceState);
		preparePrefsLayout();
		readPrefs();
		prepareMainLayout();
		if (currentChars==null) {
			getApp().getServiceHelper().doGetChars();
		} else {
			writeChars();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelableArray("currentChars", currentChars);
		savedInstanceState.putInt("currentMainId", currentMainId);
		savedInstanceState.putInt("allCount", allCount);
		savedInstanceState.putInt("goodCount", goodCount);
		savedInstanceState.putBoolean("prevHint", prevHint);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
		spinnerFlag++;
		if (Constants.DEBUG) Log.d(Constants.TAG, "spinnerFlag="+spinnerFlag);
		if (spinnerFlag>3) { //Prevent anything while preparing preferences layout
			savePrefs();
			prepareMainLayout();
			writeChars();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		if (Constants.DEBUG) Log.d(Constants.TAG, "Nothing Selected!");
	}

	@Override
	public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
		String action = requestIntent.getAction();
		if (action.equals(CharsHandler.ACTION)) {
			if (Constants.DEBUG) Log.d(Constants.TAG, "resultData:" + String.valueOf(resultData));
			currentChars = (Char[])resultData.getParcelableArray("choises");
			currentMainId = (int)(Math.random() * (numButtons-1));
			if (Constants.DEBUG) Log.d(Constants.TAG, "currentMainId:" + currentMainId);
			writeChars();
		}
	}

	@Override
	public void onClick(View view) {
		String caller = (String)view.getTag();
		if (caller.equals("main")) {
			checkResult((Button)view);
		}
		if (caller.equals("prefs")) {
			savePrefs();
			getApp().getServiceHelper().doGetChars();
		}
		if (caller.equals("hint")) {
			if (!prevHint) {
				toggleHint();
				writeChars();
			}
		}
		if (caller.equals("resetStats")) {
			allCount=0;
			goodCount=0;
			updateCount();
		}
		if (caller.equals("resetSettings")) {
			resetSettings();
			readPrefs();
			getApp().getServiceHelper().doGetChars();
		}
	}
	private void init(Bundle savedInstanceState) {
		if (savedInstanceState!=null) {
			currentChars = (Char[]) savedInstanceState.getParcelableArray("currentChars");
			currentMainId = savedInstanceState.getInt("currentMainId");
			allCount = savedInstanceState.getInt("allCount");
			goodCount = savedInstanceState.getInt("goodCount");
			prevHint = savedInstanceState.getBoolean("prevHint");
		}
		tvMain = (TextView) main.findViewById(R.id.tvMain);
		tvCount = (TextView) main.findViewById(R.id.tvCount);
		tvCount.setText(goodCount + "/" + allCount);
		oldTextColors = tvCount.getTextColors();
		btnHint = (Button) main.findViewById(R.id.btnHint);
		oldColors = btnHint.getTextColors();
		btnHint.setTag("hint");
		btnHint.setOnClickListener(this);
		btnResetSettings = (Button) prefs.findViewById(R.id.btnResetSettings);
		btnResetSettings.setTag("resetSettings");
		btnResetSettings.setOnClickListener(this);
		btnResetStats = (Button) prefs.findViewById(R.id.btnResetStats);
		btnResetStats.setTag("resetStats");
		btnResetStats.setOnClickListener(this);
		llButtons = (LinearLayout) main.findViewById(R.id.llButtons);
		typeMainSpinner = (Spinner) prefs.findViewById(R.id.typeMainSpinner);
		typeButtonsSpinner = (Spinner) prefs.findViewById(R.id.typeButtonsSpinner);
		numButtonsSpinner = (Spinner) prefs.findViewById(R.id.numButtonsSpinner);
		cbVowels = (CheckBox) prefs.findViewById(R.id.cbVowels);
		cbBase = (CheckBox) prefs.findViewById(R.id.cbBase);
		cbMarks = (CheckBox) prefs.findViewById(R.id.cbMarks);
		cbDoubles = (CheckBox) prefs.findViewById(R.id.cbDoubles);
		numsMap = new HashMap<Integer, Integer>();
		for (int i=0;i<Constants.NUM_BUTTONS.length;i++) {
			numsMap.put(Constants.NUM_BUTTONS[i], i);
			if (Constants.DEBUG) Log.d(Constants.TAG, "nums["+i+"]="+Constants.NUM_BUTTONS[i]);
		}
		sp = getSharedPreferences("hirakata", MODE_PRIVATE);
		ed = sp.edit();
	}
	private void checkResult(Button btn) {
		if (btn.getText().toString().equals(getChar(currentChars[currentMainId], 1))) {
			if (!prevHint) {
				tvCount.setTextColor(Color.GREEN);
				allCount++;
				goodCount++;
			} else {
				tvCount.setTextColor(Color.YELLOW);
				allCount++;
				toggleHint();
			}
			getApp().getServiceHelper().doGetChars();
			for (int i=0;i<numButtons;i++)
				llButtons.getChildAt(i).setEnabled(true);
		} else {
			tvCount.setTextColor(Color.RED);
			btn.setEnabled(false);
			if (!prevHint)
				allCount++;
		}
		updateCount();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				tvCount.setTextColor(oldTextColors);
			}
		}, 1000);
	}

	private void resetSettings() {
		ed.putInt("type.main", 0);
		ed.putInt("type.buttons", 2);
		ed.putInt("num.buttons", 5);
		ed.putBoolean("vowel", true);
		ed.putBoolean("base", true);
		ed.putBoolean("mark", true);
		ed.putBoolean("double", true);
		ed.commit();
	}
	private void updateCount() {
		tvCount.setText(goodCount + "/" + allCount);
	}

	private void writeChars() {
		boolean colorSet = false;
		for (int i=0;i<numButtons;i++) {
			Button currentButton = (Button) llButtons.getChildAt(i);
			Char currentChar = currentChars[i];
			if (i==currentMainId) {
				tvMain.setText(getChar(currentChar, 0));
				if (prevHint) {
					currentButton.setTextColor(Color.GREEN);
					colorSet = true;
				}
			}
			currentButton.setText(getChar(currentChar, 1));
			if (!colorSet)
				currentButton.setTextColor(oldColors);
		}
	}
	private void readPrefs() {
		int typeMainInt = sp.getInt("type.main", 0);
		int typeButtonsInt = sp.getInt("type.buttons", 2);
		numButtons = sp.getInt("num.buttons", 5);
		if (Constants.DEBUG) Log.d(Constants.TAG, "typeMainSpinner=" + String.valueOf(typeMainSpinner));
		if (Constants.DEBUG) Log.d(Constants.TAG, "typeMainStr=" + typeMainInt);
		typeMainSpinner.setSelection(typeMainInt);
		typeButtonsSpinner.setSelection(typeButtonsInt);
		numButtonsSpinner.setSelection(numsMap.get(numButtons));
		cbVowels.setChecked(sp.getBoolean("vowel", true));
		cbBase.setChecked(sp.getBoolean("base", true));
		cbMarks.setChecked(sp.getBoolean("mark", true));
		cbDoubles.setChecked(sp.getBoolean("double", true));
	}

	private void savePrefs() {
		int typeMainInt = typeMainSpinner.getSelectedItemPosition();
		int typeButtonsInt = typeButtonsSpinner.getSelectedItemPosition();
		numButtons = Integer.parseInt(numButtonsSpinner.getSelectedItem().toString());
		ed.putInt("type.main", typeMainInt);
		ed.putInt("type.buttons", typeButtonsInt);
		ed.putInt("num.buttons", numButtons);
		ed.putBoolean("vowel", cbVowels.isChecked());
		ed.putBoolean("base", cbBase.isChecked());
		ed.putBoolean("mark", cbMarks.isChecked());
		ed.putBoolean("double", cbDoubles.isChecked());
		ed.commit();
		if (Constants.DEBUG) Log.d(Constants.TAG, "num.buttons_afterSave="+ sp.getInt("num.buttons", 5));
	}
	private void prepareMainLayout() {
		llButtons.removeAllViews();
		numButtons = sp.getInt("num.buttons", 5);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i=0;i<numButtons;i++) {
			Button btn = (Button) getLayoutInflater().inflate(R.layout.template_choise_button, null);
			params.weight = 1;
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
			btn.setLayoutParams(params);
			btn.setTag("main");
			btn.setOnClickListener(this);
			llButtons.addView(btn);
		}
	}

	private void preparePrefsLayout() {
		ArrayAdapter<CharSequence> adapterString = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);
		ArrayAdapter<Integer> adapterInts = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, Constants.NUM_BUTTONS);
		adapterString.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterInts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeMainSpinner.setAdapter(adapterString);
		typeMainSpinner.setOnItemSelectedListener(this);
		typeButtonsSpinner.setAdapter(adapterString);
		typeButtonsSpinner.setOnItemSelectedListener(this);
		numButtonsSpinner.setAdapter(adapterInts);
		numButtonsSpinner.setOnItemSelectedListener(this);
		cbVowels.setOnClickListener(this);
		cbVowels.setTag("prefs");
		cbBase.setOnClickListener(this);
		cbBase.setTag("prefs");
		cbMarks.setOnClickListener(this);
		cbMarks.setTag("prefs");
		cbDoubles.setOnClickListener(this);
		cbDoubles.setTag("prefs");
	}

	public String getChar(Char c, int type) { //0 - Main char, 1 - Choise
		int typeMainInt = sp.getInt("type.main", 0);
		int typeChoiseInt = sp.getInt("type.buttons", 2);
		switch (type==0?typeMainInt:typeChoiseInt) {
			case 0:
				return c.getHiragana();
			case 1:
				return c.getKatakana();
			case 2:
				return c.getRomaji();
			case 3:
				return c.getPolivanov();
		}
		return null;
	}
	public void toggleHint() {
		this.prevHint = !this.prevHint;
	}
}
