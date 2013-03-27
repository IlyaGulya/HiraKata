package me.amasawa.hirakata;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import me.amasawa.hirakata.handlers.CharsHandler;
import me.amasawa.hirakata.interfaces.ServiceCallbackListener;
import me.amasawa.hirakata.services.MainService;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceHelper {
	private ArrayList<ServiceCallbackListener> currentListeners = new ArrayList<ServiceCallbackListener>();
	private AtomicInteger idCounter = new AtomicInteger();
	private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();
	private Application application;
	ServiceHelper(Application app) {
		this.application = app;
	}

	public void addListener(ServiceCallbackListener currentListener) {
		currentListeners.add(currentListener);
	}

	public void removeListener(ServiceCallbackListener currentListener) {
		currentListeners.remove(currentListener);
	}

	private Intent createIntent(final Context context, String actionLogin, final int requestId) {
		Intent i = new Intent(context, MainService.class);
		i.setAction(actionLogin);

		i.putExtra(MainService.EXTRA_STATUS_RECEIVER, new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (Constants.DEBUG) Log.d(Constants.TAG, "onReceiveResult.resultCode:" + resultCode);
				for (ServiceCallbackListener cl : currentListeners) {
					if (Constants.DEBUG) Log.d(Constants.TAG, "onReceiveResult.cl:" + cl.toString());
				}
				Intent originalIntent = pendingActivities.get(requestId);
				if (isPending(requestId)) {
					pendingActivities.remove(requestId);

					for (ServiceCallbackListener currentListener : currentListeners) {
						if (currentListener != null) {
							currentListener.onServiceCallback(requestId, originalIntent, resultCode, resultData);
						}
					}
				}
			}
		});
		return i;
	}
	public boolean isPending(int requestId) {
		return pendingActivities.get(requestId) != null;
	}

	private int createId() {
		return idCounter.getAndIncrement();
	}

	private int runRequest(final int requestId, Intent i) {
		if (Constants.DEBUG) Log.d(Constants.TAG, "runRequest()");
		if (Constants.DEBUG) Log.d(Constants.TAG, "Intent:"+i.toString());
		pendingActivities.append(requestId, i);
		application.startService(i);
		return requestId;
	}

	public int doGetChars() {
		final int requestId = createId();
		Intent i = createIntent(application, CharsHandler.ACTION, requestId);
		return runRequest(requestId, i);
	}
}
