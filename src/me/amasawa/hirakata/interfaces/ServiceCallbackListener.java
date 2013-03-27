package me.amasawa.hirakata.interfaces;

import android.content.Intent;
import android.os.Bundle;

public interface ServiceCallbackListener {
	void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data);
}