package com.raxdenstudios.cron.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.raxdenstudios.cron.CronHandler;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class CronBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		CronHandler cronHandler = new CronHandler(context);
		cronHandler.startAll()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeWith(new DisposableCompletableObserver() {
					@Override
					public void onComplete() {
						dispose();
					}

					@Override
					public void onError(Throwable e) {
						dispose();
					}
				});
	}

}
