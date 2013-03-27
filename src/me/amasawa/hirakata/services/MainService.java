package me.amasawa.hirakata.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.Constants;
import me.amasawa.hirakata.data.Actions;
import me.amasawa.hirakata.handlers.BaseIntentHandler;

public class MainService extends IntentService {
	private BaseApplication application;
	public static final String TAG = "MainService";
	public Actions actions = null;
	public final static String EXTRA_STATUS_RECEIVER = "me.amasawa.hirakata.service.main.receiver";

	public MainService() {
		super(TAG);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		this.application = (BaseApplication) getApplication();
		if (Constants.DEBUG) Log.d(Constants.TAG, "MainService.application:" + String.valueOf(application));
		this.actions = new Actions(application);
	}
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if(!TextUtils.isEmpty(action)) {
			if (Constants.DEBUG) Log.d(Constants.TAG, action);
			final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
			BaseIntentHandler handler = actions.getHandler(action);
			if (handler!=null)
				handler.execute(intent, this, receiver, application);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
