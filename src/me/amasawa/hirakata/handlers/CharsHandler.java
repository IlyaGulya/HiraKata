package me.amasawa.hirakata.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.data.Char;

public class CharsHandler extends BaseIntentHandler{
	public static final String ACTION = "me.amasawa.hirakata.action.WRITE_CHARS";
	public CharsHandler(BaseApplication application) {
		super(application);
	}

	public void doExecute(Intent intent, Context context, ResultReceiver callback, BaseApplication application) {
		Char[] choiseChars = getAbc().getChoiseChars();
		Bundle result = new Bundle();
		result.putParcelableArray("choises", choiseChars);
		sendUpdate(0, result);
	}
}
