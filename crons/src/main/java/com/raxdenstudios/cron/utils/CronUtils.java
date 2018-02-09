package com.raxdenstudios.cron.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.raxdenstudios.commons.util.ConvertUtils;
import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.model.Cron;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Angel on 07/02/2018.
 */

public class CronUtils {

    public static void setAlarmManager(Context context, Cron cron) {
        if (cron != null && cron.getTriggerAtTime() > 0) {
            AlarmManager manager = createAlarmManager(context);
            PendingIntent pendingIntent = initPendingIntent(context, cron);
            if (manager != null && pendingIntent != null) {
                // Schedule the cron!
                manager.cancel(pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(cron.getType(), cron.getTriggerAtTime(), pendingIntent);
                } else {
                    manager.set(cron.getType(), cron.getTriggerAtTime(), pendingIntent);
                }
            }
        }
    }

    public static void cancelAlarmManager(Context context, Cron cron) {
        if (cron != null) {
            AlarmManager manager = createAlarmManager(context);
            PendingIntent pendingIntent = initPendingIntent(context, cron);
            if (pendingIntent != null) {
                // Cancel the cron!
                manager.cancel(pendingIntent);
            }
        }
    }

    private static PendingIntent initPendingIntent(Context context, Cron cron) {
        Intent intent = new Intent();
        intent.setAction(Utils.getPackageName(context) + ".CRON");
        intent.putExtra(Cron.class.getSimpleName(), cron.getId());
        return PendingIntent.getService(context, ConvertUtils.longToInt(cron.getId()), intent, 0);
    }

    private static AlarmManager createAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static String currentDateTime() {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String triggerAtTime(Cron cron) {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date(cron.getTriggerAtTime()));
    }

    public static String intervalInSeconds(Cron cron) {
        return ((float) (cron.getInterval() / (1000))) + " seconds";
    }

    public static String nextLaunchInSeconds(Cron cron) {
        return ((float) ((cron.getTriggerAtTime() - Calendar.getInstance().getTimeInMillis()) / (1000))) + " seconds";
    }

}
