package com.raxdenstudios.cron;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.util.CronUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CronReceiver extends BroadcastReceiver {

    private static final String TAG = CronReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		Realm mRealm = Realm.getDefaultInstance();
		RealmResults<Cron> result = mRealm.where(Cron.class).findAll();

		List<Cron> crons = Arrays.asList(result.toArray(new Cron[result.size()]));
		Log.d(TAG, "[onReceive] "+crons.size() + " crons was found.");

		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		long now = Calendar.getInstance().getTimeInMillis();

		for (Cron cron : crons) {
			long triggerAtTime = cron.getTriggerAtTime();
			if (triggerAtTime < now) {
				do {
					triggerAtTime = triggerAtTime + cron.getInterval();
				} while (triggerAtTime < now);

				mRealm.beginTransaction();
				cron.setTriggerAtTime(triggerAtTime);
				mRealm.commitTransaction();

				PendingIntent mCronSender = initPendingIntent(context, cron);
				if (mCronSender != null) {
					// Schedule the cron!
					alarmManager.cancel(mCronSender);
					if (cron.getInterval() > 0) {
						alarmManager.setRepeating(cron.getType(), cron.getTriggerAtTime(), cron.getInterval(), mCronSender);
					} else {
						alarmManager.set(cron.getType(), cron.getTriggerAtTime(), mCronSender);
					}
					Log.d(TAG, "[onReceive] AlarmManager updated with cron: "+ CronUtils.dump(cron));
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
