package me.amasawa.hirakata.data;

import me.amasawa.hirakata.BaseApplication;
import me.amasawa.hirakata.handlers.BaseIntentHandler;
import me.amasawa.hirakata.handlers.CharsHandler;

import java.util.HashMap;

public class Actions {
	private HashMap<String, BaseIntentHandler> actions;
	public Actions(BaseApplication application) {
		this.actions = new HashMap<String, BaseIntentHandler>();
		this.actions.put(CharsHandler.ACTION, new CharsHandler(application));
	}

	public BaseIntentHandler getHandler(String action) {
		return actions.get(action);
	}
}
