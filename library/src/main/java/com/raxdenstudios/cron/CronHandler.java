package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.data.CronDAO;
import com.raxdenstudios.cron.data.CronDAOImpl;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.util.CronUtils;

import java.util.List;

import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
	private CronDAO mCronDAO;

    public CronHandler(Context context) {
        this(context, Realm.getDefaultInstance());
    }

	public CronHandler(Context context, Realm realm) {
		mContext = context;
		mCronDAO = new CronDAOImpl(context, realm);
	}

	public void start(final Cron cron, final StartCronCallbacks callbacks) {
        mCronDAO.create(cron)
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
        mCronDAO.remove(cronId)
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
        mCronDAO.removeAll()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Cron>>() {
                    @Override
                    public void call(List<Cron> crons) {
                        for (Cron cron : crons) {
                            Log.d(TAG, "==[Cron removed]["+cron.getId()+"]===================");
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
            Log.d(TAG, "==[Cron started]["+cron.getId()+"]===================");
			Log.d(TAG, CronUtils.dump(cron));

			AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
		}
	}
	
	public static void finishNotPersist(Context ocntext, Cron cron) {
		if (cron != null) {
			Log.d(TAG, "==[Cron finished]["+cron.getId()+"]===================");
            Log.d(TAG, CronUtils.dump(cron));

			AlarmManager manager = (AlarmManager) ocntext.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(ocntext, cron);
		    
			if (mCronSender != null) {
				// Cancel the cron!
				manager.cancel(mCronSender);
			}
		}
	}
	
	protected static PendingIntent initPendingIntent(Context context, Cron cron) {
		Intent intent = new Intent();
        intent.setAction(Utils.getPackageName(context)+".CRON");
		intent.putExtra(Cron.class.getSimpleName(), cron.getId());
		return PendingIntent.getService(context, ConvertUtils.longToInt(cron.getId()), intent, 0);
	}

}
