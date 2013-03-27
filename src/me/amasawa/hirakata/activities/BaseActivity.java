package me.amasawa.hirakata.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.analytics.tracking.android.EasyTracker;
import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.ServiceHelper;
import me.amasawa.hirakata.interfaces.ServiceCallbackListener;

public abstract class BaseActivity extends Activity implements ServiceCallbackListener {
	private ServiceHelper serviceHelper;
	protected BaseApplication getApp() {
		return (BaseApplication) getApplication();
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serviceHelper = getApp().getServiceHelper();
	}
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStart(this);
	}
	protected void onResume() {
		super.onResume();
		serviceHelper.addListener(this);
	}
	protected void onPause() {
		super.onPause();
		serviceHelper.removeListener(this);
	}
	protected ServiceHelper getServiceHelper() {
		return serviceHelper;
	}
	public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) { }
}