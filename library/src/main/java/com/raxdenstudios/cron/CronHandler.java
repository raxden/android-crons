package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.util.CronUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CronHandler {

    private static final String TAG = CronHandler.class.getSimpleName();

	public interface StartCronCallbacks {
		void onCronStarted(Cron cron);
		void onCronError(String errorMessage);
	}

	public interface FinishCronCallbacks {
		void onCronFinished(long cronId);
		void onCronError(String errorMessage);
	}
	
	private Context mContext;
	private Realm mRealm;
	
	public CronHandler(Context context, Realm realm) {
		mContext = context;
		mRealm = realm;
	}

	public void start(final Cron cron, final StartCronCallbacks callbacks) {
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
                if (cron.getId() == 0) {
                    cron.setId(realm.where(Cron.class).max("id").longValue() + 1);
                }
				realm.copyToRealmOrUpdate(cron);
			}
		}, new Realm.Transaction.Callback() {
			@Override
			public void onSuccess() {
                Log.d(TAG, "==[Cron created]["+cron.getId()+"]===================");
				startNotPersist(cron);
                if (callbacks != null) callbacks.onCronStarted(cron);
			}

			@Override
			public void onError(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if (callbacks != null) callbacks.onCronError(e.getMessage());
			}
		});
	}

	public void finish(final long cronId, final FinishCronCallbacks callbacks) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final Cron cron = realm.where(Cron.class).equalTo("id", cronId).findFirst();
                if (cron != null) {
                    finishNotPersist(cron);
                    cron.removeFromRealm();
                } else {
                    if (callbacks != null) callbacks.onCronError("Cron with "+cronId+" not exists");
                }
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "==[Cron removed]["+cronId+"]===================");
                if (callbacks != null) callbacks.onCronFinished(cronId);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                if (callbacks != null) callbacks.onCronError(e.getMessage());
            }
        });
	}

	public void finish(final Cron cron, final FinishCronCallbacks callbacks) {
        finish(cron.getId(), callbacks);
	}
	
	public void finishAll(final FinishCronCallbacks callbacks) {
		final List<Long> crons = new ArrayList<>();
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				RealmResults<Cron> result = realm.where(Cron.class).findAll();
                if (result.size() == 0) {
                    if (callbacks != null) callbacks.onCronError("Crons not found.");
                } else {
                    Iterator<Cron> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Cron cron = iterator.next();
                        finishNotPersist(cron);
                        crons.add(cron.getId());
                    }
                    result.clear();
                }
			}
		}, new Realm.Transaction.Callback() {
			@Override
			public void onSuccess() {
                for (Long cronId : crons) {
                    if (callbacks != null) callbacks.onCronFinished(cronId);
                }
			}

			@Override
			public void onError(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if (callbacks != null) callbacks.onCronError(e.getMessage());
			}
		});
	}	
	
	public void startNotPersist(Cron cron) {
		if (cron != null && cron.getTriggerAtTime() > 0) {
            Log.d(TAG, "==[Cron started]["+cron.getId()+"]===================");
			Log.d(TAG, CronUtils.dump(cron));

			AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(mContext, cron);
			
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
	
	public void finishNotPersist(Cron cron) {
		if (cron != null) {
			Log.d(TAG, "==[Cron finished]["+cron.getId()+"]===================");
            Log.d(TAG, CronUtils.dump(cron));

			AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(mContext, cron);
		    
			if (mCronSender != null) {
				// Cancel the cron!
				manager.cancel(mCronSender);
			}
		}
	}
	
	protected PendingIntent initPendingIntent(Context context, Cron cron) {
		Intent intent = new Intent();
        intent.setAction(Utils.getPackageName(context)+".CRON");
		intent.putExtra(Cron.class.getSimpleName(), cron.getId());
		return PendingIntent.getService(context, ConvertUtils.longToInt(cron.getId()), intent, 0);
	}

}
