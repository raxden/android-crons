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
		void onCronFinished(Cron cron);
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
				startNotPersist(cron, callbacks);
			}

			@Override
			public void onError(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if (callbacks != null) callbacks.onCronError(e.getMessage());
			}
		});
	}
		
	public void finish(final Cron cron, final FinishCronCallbacks callbacks) {
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				cron.removeFromRealm();
			}
		}, new Realm.Transaction.Callback() {
			@Override
			public void onSuccess() {
				finishNotPersist(cron, callbacks);
			}

			@Override
			public void onError(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if (callbacks != null) callbacks.onCronError(e.getMessage());
			}
		});
	}
	
	public void finishAll(final FinishCronCallbacks callbacks) {
		final List<Cron> crons = new ArrayList<>();
		mRealm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				RealmResults<Cron> result = realm.where(Cron.class).findAll();
				crons.addAll(Arrays.asList(result.toArray(new Cron[result.size()])));
			}
		}, new Realm.Transaction.Callback() {
			@Override
			public void onSuccess() {
				for (Cron cron : crons) {
					finishNotPersist(cron, callbacks);
				}
			}

			@Override
			public void onError(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				if (callbacks != null) callbacks.onCronError(e.getMessage());
			}
		});
	}	
	
	public void startNotPersist(Cron cron, StartCronCallbacks callbacks) {
		if (cron != null && cron.getTriggerAtTime() > 0) {
            Log.d(TAG, "==[Cron started]==================================");
			Log.d(TAG, CronUtils.dump(cron));
            Log.d(TAG, "=====================================================");

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
		        
		        if (callbacks != null) {
		        	callbacks.onCronStarted(cron);
		        }
			}
		}
	}
	
	public void finishNotPersist(Cron cron, FinishCronCallbacks callbacks) {
		if (cron != null) {
			Log.d(TAG, "==[Cron finished]=================================");
            Log.d(TAG, CronUtils.dump(cron));
			Log.d(TAG, "=====================================================");

			AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(mContext, cron);
		    
			if (mCronSender != null) {
				// Cancel the cron!
				manager.cancel(mCronSender);
				
				if (callbacks != null) {
					callbacks.onCronFinished(cron);
				}
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
