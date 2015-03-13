package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.cron.db.CronManager;
import com.raxdenstudios.cron.db.CronOpenHelper;
import com.raxdenstudios.cron.db.model.CronDBConstants;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.db.DBManager;

import java.util.Date;
import java.util.List;

public class CronHandler {

    private static final String TAG = CronHandler.class.getSimpleName();

	public interface StartCronCallbacks {
		public void onCronStarted(Cron cron);
	}

	public interface FinishCronCallbacks {
		public void onCronFinished(Cron cron);
	}
	
	private Context context;
	private Class<?> cronService;
	private CronManager cronManager;
	
	public CronHandler(Context context, CronOpenHelper oh, Class<?> cronService) {
		this(context, new CronManager(oh), cronService);
	}
	
	public CronHandler(Context context, CronManager cronManager, Class<?> cronService) {
		this.context = context;
		this.cronManager = cronManager;
		this.cronService = cronService;
	}
	
	public void close() {
		if (cronManager != null) {
			cronManager.close();
		}
	}
	
	public CronManager getCronManager() {
		return cronManager;
	}

	public void start(long triggerAtTime, long interval, StartCronCallbacks callbacks) {
		start(triggerAtTime, interval, AlarmManager.RTC_WAKEUP, callbacks);
	}
	
	public void start(long triggerAtTime, long interval, int type, StartCronCallbacks callbacks) {
		start(new Cron(0, triggerAtTime, interval, type), callbacks);
	}
	
	public void start(Cron cron, final StartCronCallbacks callbacks) {
		cronManager.save(cron, new DBManager.DBSaveCallbacks<Cron>() {

			@Override
			public void dataSaved(Cron cron) {
				startNotPersist(cron, callbacks);
			}
		});
	}
		
	public void finish(Cron cron, final FinishCronCallbacks callbacks) {
		cronManager.delete(cron, new DBManager.DBDeleteCallbacks<Cron>() {

			@Override
			public void dataDeleted(Cron cron) {
				finishNotPersist(cron, callbacks);
			}
		});
	}
	
	public void finishAll(final FinishCronCallbacks callbacks) {
		cronManager.deleteAll(new DBManager.DBDeleteAllCallbacks<Cron>() {

			@Override
			public void dataDeleted(List<Cron> crons) {
				for (Cron cron : crons) {
					finishNotPersist(cron, callbacks);
				}
			}
		});
	}	
	
	public void startNotPersist(Cron cron, StartCronCallbacks callbacks) {
		if (cron != null && cron.getTriggerAtTime() > 0) {
			Log.d(TAG, "start cron: " + cron.toString());
			Log.d(TAG, "     now 		  - " + new Date().toString());
			Log.d(TAG, "     triggerAtTime - " + new Date(cron.getTriggerAtTime()).toString());
			Log.d(TAG, "     interval 	  - " + (cron.getInterval() / AlarmManager.INTERVAL_HOUR) + " hours");
			
			AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(context, cronService, ConvertUtils.longToInt(cron.getId()));
			
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
			Log.d(TAG, "finish cron: " + cron.toString());
			
			AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent mCronSender = initPendingIntent(context, cronService, ConvertUtils.longToInt(cron.getId()));
		    
			if (mCronSender != null) {
				// Cancel the cron!
				manager.cancel(mCronSender);
				
				if (callbacks != null) {
					callbacks.onCronFinished(cron);
				}
			}
		}
	}
	
	protected PendingIntent initPendingIntent(Context context, Class<?> cronService, int cronId) {
		Intent intent = new Intent(context, cronService);
		intent.putExtra(CronDBConstants.CRON_ID, cronId);
		return PendingIntent.getService(context, cronId, intent, 0);
	}		
}
