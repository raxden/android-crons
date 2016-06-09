package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.factory.CronFactoryService;
import com.raxdenstudios.cron.model.Cron;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class CronHandler {

    private static final String TAG = CronHandler.class.getSimpleName();

	public interface StartCronCallbacks {
		void onCronStarted(Cron cron);
		void onCronError(String errorMessage);
	}

	public interface FinishCronCallbacks {
		void onCronFinished(Cron cron);
		void onCronError(String errorMessage);
	}

    public interface FinishAllCronCallbacks {
        void onCronsFinished(List<Cron> crons);
        void onCronsError(String errorMessage);
    }

	private Context mContext;
	private CronService mCronService;

	public CronHandler(Context context) {
		mContext = context;
        mCronService = new CronFactoryService(context);
	}

	public void start(final Cron cron, final StartCronCallbacks callbacks) {
        Log.d(TAG, "Prepare to start cron["+cron.getId()+"]");
        mCronService.save(cron)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Cron>() {
                    @Override
                    public void call(Cron cron) {
                        startNotPersist(mContext, cron);
                        if (callbacks != null) callbacks.onCronStarted(cron);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        if (callbacks != null) callbacks.onCronError(t.getMessage());
                    }
                });
	}

	public void finish(final long cronId, final FinishCronCallbacks callbacks) {
        mCronService.delete(cronId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Cron>() {
                    @Override
                    public void call(Cron cron) {
                        finishNotPersist(mContext, cron);
                        if (callbacks != null) callbacks.onCronFinished(cron);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        if (callbacks != null) callbacks.onCronError(t.getMessage());
                    }
                });
    }

	public void finishAll(final FinishAllCronCallbacks callbacks) {
        mCronService.getAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<Cron>, Observable<List<Cron>>>() {
                    @Override
                    public Observable<List<Cron>> call(List<Cron> crons) {
                        List<Observable<Cron>> obs = new ArrayList<>();
                        for (Cron cron: crons) {
                            obs.add(mCronService.delete(cron.getId())
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
                        for (Cron cron : crons) {
                            finishNotPersist(mContext, cron);
                        }
                        if (callbacks != null) callbacks.onCronsFinished(crons);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        if (callbacks != null) callbacks.onCronsError(t.getMessage());
                    }
                });
	}	
	
	public static void startNotPersist(Context context, Cron cron) {
		if (cron != null && cron.getTriggerAtTime() > 0) {
            AlarmManager manager = createAlarmManager(context);
			PendingIntent mCronSender = initPendingIntent(context, cron);
			
			if (manager != null && mCronSender != null) {
			    // Schedule the cron!
				manager.cancel(mCronSender);  
				if (cron.getInterval() > 0) {
					manager.setRepeating(cron.getType(), cron.getTriggerAtTime(), cron.getInterval(), mCronSender);
				} else {
					manager.set(cron.getType(), cron.getTriggerAtTime(), mCronSender);
				}
			}
            Log.d(TAG, "==[Cron started]["+cron.getId()+"]===================");
		}
	}
	
	public static void finishNotPersist(Context context, Cron cron) {
		if (cron != null) {
			AlarmManager manager = createAlarmManager(context);
			PendingIntent mCronSender = initPendingIntent(context, cron);
		    
			if (mCronSender != null) {
				// Cancel the cron!
				manager.cancel(mCronSender);
			}
            Log.d(TAG, "==[Cron finished]["+cron.getId()+"]===================");
		}
	}
	
	protected static PendingIntent initPendingIntent(Context context, Cron cron) {
		Intent intent = new Intent();
        intent.setAction(Utils.getPackageName(context)+".CRON");
		intent.putExtra(Cron.class.getSimpleName(), cron.getId());
		return PendingIntent.getService(context, ConvertUtils.longToInt(cron.getId()), intent, 0);
	}

    protected static AlarmManager createAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

}
