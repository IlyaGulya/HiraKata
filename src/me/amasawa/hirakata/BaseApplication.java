package me.amasawa.hirakata;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
	private ServiceHelper serviceHelper;
	private DBHelper mHelper;
	@Override
	public void onCreate() {
		super.onCreate();
		serviceHelper = new ServiceHelper(this);
		mHelper = new DBHelper(this);
	}

	public ServiceHelper getServiceHelper() {
		return serviceHelper;
	}

	public static BaseApplication getApplication(Context context) {
		if (context instanceof BaseApplication) {
			return (BaseApplication) context;
		}
		return (BaseApplication) context.getApplicationContext();
	}
	public DBHelper getDBHelper() {
		return this.mHelper;
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mHelper.close();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		mHelper.close();
	}
}