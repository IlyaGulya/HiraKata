package me.amasawa.hirakata.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.Constants;
import me.amasawa.hirakata.data.Alphabet;

public abstract class BaseIntentHandler {
	public static final int SUCCESS_RESPONSE = 0;

	public static final int FAILURE_RESPONSE = 1;

	public final void execute(Intent intent, Context context, ResultReceiver callback, BaseApplication app) {
		this.callback = callback;
		doExecute(intent, context, callback, app);
	}

	public abstract void doExecute(Intent intent, Context context, ResultReceiver callback, BaseApplication app);
	private Alphabet abc;
	private ResultReceiver callback;
	public BaseIntentHandler(BaseApplication application) {
		if (Constants.DEBUG) Log.d(Constants.TAG, "BaseIntentHandler.application:" + String.valueOf(application));
		this.abc = new Alphabet(application);
	}
	private int result;

	public int getResult() {
		return result;
	}
	public Alphabet getAbc() {
		return abc;
	}
	protected void sendUpdate(int resultCode, Bundle data) {
		result = resultCode;
		if (callback != null) {
			callback.send(resultCode, data);
		}
	}

}