package com.raxdenstudios.cron.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.factory.CronFactoryService;
import com.raxdenstudios.cron.model.Cron;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class CronBootReceiver extends BroadcastReceiver {

    private static final String TAG = CronBootReceiver.class.getSimpleName();

	@Override
	public void onReceive(final Context context, Intent intent) {

        Log.d(TAG, "CronBootReceiver started....");

		final CronService cronService = new CronFactoryService(context);
		cronService.getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
				.map(new Func1<List<Cron>, List<Cron>>() {
					@Override
					public List<Cron> call(List<Cron> crons) {
                        Log.d(TAG, crons.size() + " crons detected on bd");
						long now = Calendar.getInstance().getTimeInMillis();
						for (Cron cron : crons) {
                            Log.d(TAG, cron.toString());
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
				.flatMap(new Func1<List<Cron>, Observable<List<Cron>>>() {
					@Override
					public Observable<List<Cron>> call(List<Cron> crons) {
                        List<Observable<Cron>> obs = new ArrayList<>();
                        for (Cron cron: crons) {
                            obs.add(cronService.save(cron)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread()));
                        }
                        return Observable.zip(obs, new FuncN<List<Cron>>() {
                            @Override
                            public List<Cron> call(Object... args) {
                                List<Cron> crons = new ArrayList<>();
                                for (int i = 0; i < args.length; i++) {
                                    crons.add((Cron)args[i]);
                                }
                                return crons;
                            }
                        });
					}
				})
				.subscribe(new Action1<List<Cron>>() {
					@Override
					public void call(List<Cron> crons) {
                        Log.d(TAG, crons.size() + " crons updated, prepare to start...");
						for (Cron cron : crons) {
							CronHandler.startNotPersist(context, cron);
						}
                        Log.d(TAG, "CronBootReceiver finished....");
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable t) {
						Log.e(TAG, t.getMessage(), t);
                        Log.d(TAG, "CronBootReceiver finished....");
					}
				});
	}

}
