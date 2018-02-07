package com.raxdenstudios.cron;

import android.content.Context;
import android.util.Log;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.factory.CronFactoryService;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.utils.CronUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CronHandler {

    private static final String TAG = CronHandler.class.getSimpleName();

    private final Context context;
    private final CronService cronService;

    public CronHandler(Context context) {
        this.context = context;
        this.cronService = new CronFactoryService(context);
    }

    public CronHandler(Context context, CronService cronService) {
        this.context = context;
        this.cronService = cronService;
    }

    public Completable start(final Cron cron) {
        return cronService.save(cron)
                .map(new Function<Cron, Cron>() {
                    @Override
                    public Cron apply(Cron cron) throws Exception {
                        CronUtils.setAlarmManager(context, cron);
                        return cron;
                    }
                })
                .doOnSuccess(new Consumer<Cron>() {
                    @Override
                    public void accept(Cron cron) throws Exception {
                        Log.d(TAG, "Cron[" + cron.getId() + "] started at " + CronUtils.currentDateTime() + " with interval " + CronUtils.intervalInSeconds(cron) + ". Next launch in " + CronUtils.nextLaunchInSeconds(cron) + " " + CronUtils.triggerAtTime(cron));
                    }
                })
                .toCompletable();
    }

    public Completable finish(final long cronId) {
        return cronService.get(cronId)
                .flatMap(new Function<Cron, SingleSource<Cron>>() {
                    @Override
                    public SingleSource<Cron> apply(Cron cron) throws Exception {
                        CronUtils.cancelAlarmManager(context, cron);
                        return cronService.delete(cron.getId()).toSingleDefault(cron);
                    }
                })
                .doOnSuccess(new Consumer<Cron>() {
                    @Override
                    public void accept(Cron cron) throws Exception {
                        Log.d(TAG, "Cron[" + cron.getId() + "] finished at " + CronUtils.currentDateTime());
                    }
                })
                .toCompletable();
    }

    public Completable startAll() {
        return cronService.getAll()
                .map(new Function<List<Cron>, List<Cron>>() {
                    @Override
                    public List<Cron> apply(List<Cron> crons) throws Exception {
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
                .flatMapCompletable(new Function<List<Cron>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<Cron> crons) throws Exception {
                        List<Single<Cron>> obs = new ArrayList<>();
                        for (Cron cron : crons) {
                            obs.add(start(cron).toSingleDefault(cron).subscribeOn(Schedulers.newThread()));
                        }
                        return Single.zip(obs, new Function<Object[], List<Cron>>() {
                            @Override
                            public List<Cron> apply(Object[] objects) throws Exception {
                                List<Cron> crons = new ArrayList<>();
                                for (int i = 0; i < objects.length; i++) {
                                    crons.add((Cron) objects[i]);
                                }
                                return crons;
                            }
                        }).toCompletable();
                    }
                });
    }

    public Completable finishAll() {
        return cronService.getAll()
                .flatMapCompletable(new Function<List<Cron>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<Cron> crons) throws Exception {
                        List<Single<Cron>> obs = new ArrayList<>();
                        for (Cron cron : crons) {
                            obs.add(finish(cron.getId()).toSingleDefault(cron).subscribeOn(Schedulers.io()));
                        }
                        return Single.zip(obs, new Function<Object[], List<Cron>>() {
                            @Override
                            public List<Cron> apply(Object[] objects) throws Exception {
                                List<Cron> crons = new ArrayList<>();
                                for (int i = 0; i < objects.length; i++) {
                                    crons.add((Cron) objects[i]);
                                }
                                return crons;
                            }
                        }).toCompletable();
                    }
                });
    }

}
