package com.raxdenstudios.cron.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.data.CronDAO;
import com.raxdenstudios.cron.data.CronDAOImpl;
import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CronReceiver extends BroadcastReceiver {

    private static final String TAG = CronReceiver.class.getSimpleName();

	@Override
	public void onReceive(final Context context, Intent intent) {

		final CronDAO cronDAO = new CronDAOImpl(context);
		cronDAO.findAll()
				.subscribeOn(Schedulers.immediate())
				.observeOn(Schedulers.immediate())
				.map(new Func1<List<Cron>, List<Cron>>() {
					@Override
					public List<Cron> call(List<Cron> crons) {
						long now = Calendar.getInstance().getTimeInMillis();
						for (Cron cron : crons) {
							long triggerAtTime = cron.getTriggerAtTime();
							if (triggerAtTime < now) {
								do {
									triggerAtTime = triggerAtTime + cron.getInterval();
								} while (triggerAtTime < now);
								cron.setTriggerAtTime(triggerAtTime);
							}
						}
						return crons;
					}
				})
				.concatMap(new Func1<List<Cron>, Observable<List<Cron>>>() {
					@Override
					public Observable<List<Cron>> call(List<Cron> crons) {
						return cronDAO.updateAll(crons);
					}
				})
				.subscribe(new Action1<List<Cron>>() {
					@Override
					public void call(List<Cron> crons) {
						for (Cron cron : crons) {
							CronHandler.startNotPersist(context, cron);
						}
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable t) {
						Log.e(TAG, t.getMessage(), t);
					}
				});
	}

	protected PendingIntent initPendingIntent(Context context, Cron cron) {
		Intent intent = new Intent();
		intent.setAction(Utils.getPackageName(context)+".CRON");
		intent.putExtra(Cron.class.getSimpleName(), cron.getId());
		return PendingIntent.getService(context, ConvertUtils.longToInt(cron.getId()), intent, 0);
	}
	
}
