package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.cron.db.CronOpenHelper;
import com.raxdenstudios.cron.db.model.CronDBConstants;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.db.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class CronReceiver extends BroadcastReceiver {

    private static final String TAG = CronReceiver.class.getSimpleName();

    public abstract CronOpenHelper initCronOpenHelper(Context context);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		
		List<Cron> crons = new ArrayList<Cron>();
		
		CronOpenHelper oh = null;
		SQLiteDatabase db = null;
		
		try {
		
			oh = initCronOpenHelper(context);
			db = oh.getWritableDatabase();
			
			Log.d(TAG, "beginTransaction");
			db.beginTransaction();
		
			// find all cron' s
			Cursor cursor = DBManager.select(db, CronDBConstants.CRON_TABLE_NAME, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					crons.add(new Cron(cursor));
				} while (cursor.moveToNext());
			}
			
			if (crons != null && !crons.isEmpty()) {
				Log.d(TAG, crons.size() + " crons was found.");
				Log.d(TAG, "cronService: " + getCronService().getSimpleName());
				
				AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				
	    		long nowTime = Calendar.getInstance().getTimeInMillis();
				
				for (Cron cron : crons) {
	
		    		long triggerAtTime = cron.getTriggerAtTime();
					
					if (triggerAtTime < nowTime) {
	         		   do {
	         			  triggerAtTime = triggerAtTime + cron.getInterval();
	        		   } while (triggerAtTime < nowTime);
					
	         		   cron.setTriggerAtTime(triggerAtTime);
	         		   DBManager.update(db, CronDBConstants.CRON_TABLE_NAME, cron.readContentValues(), CronDBConstants.CRON_ID+"=?", new String[] { Long.toString(cron.getId()) });
					}
					
					PendingIntent mCronSender = initPendingIntent(context, getCronService(), ConvertUtils.longToInt(cron.getId()));
					
					if (mCronSender != null) {
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
			
			db.setTransactionSuccessful();
			Log.d(TAG, "transactionSuccessful");
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (db != null) db.endTransaction();
			if (oh != null) oh.close();
		}

	}
	
	public abstract Class<?> getCronService();
	
	protected PendingIntent initPendingIntent(Context context, Class<?> cronService, int cronId) {
		Intent intent = new Intent(context, cronService);
		intent.putExtra(CronDBConstants.CRON_ID, cronId);
		return PendingIntent.getService(context, cronId, intent, 0);
	}
	
}
