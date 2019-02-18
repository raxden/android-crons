package com.raxdenstudios.cron.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.raxdenstudios.cron.CronHandler
import com.raxdenstudios.cron.CronManager
import com.raxdenstudios.cron.model.Cron
import java.text.SimpleDateFormat
import java.util.*

object CronUtils {

    private val TAG = CronUtils::class.java.simpleName

    internal var isForeground: Boolean = false

    fun setAlarmManager(context: Context, cron: Cron) {
        cron.takeIf { it.triggerAtTime > 0 }?.apply {
            createAlarmManager(context).apply {
                val pendingIntent = initPendingIntent(context, cron)
                cancel(pendingIntent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    setExact(cron.type, cron.triggerAtTime, pendingIntent)
                } else {
                    set(type, cron.triggerAtTime, pendingIntent)
                }
            }
        }
    }

    fun cancelAlarmManager(context: Context, cron: Cron) {
        createAlarmManager(context).apply {
            val pendingIntent = initPendingIntent(context, cron)
            cancel(pendingIntent)
        }
    }

    fun currentDateTime() = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date())

    fun triggerAtTime(cron: Cron) = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(Date(cron.triggerAtTime))

    fun intervalInSeconds(cron: Cron) = StringBuffer((cron.interval / 1000).toFloat().toString()).append(" seconds")

    fun nextLaunchInSeconds(cron: Cron) = StringBuffer(((cron.triggerAtTime - Calendar.getInstance().timeInMillis) / 1000).toFloat().toString()).append(" seconds")

    private fun createAlarmManager(context: Context) = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun initPendingIntent(context: Context, cron: Cron): PendingIntent {
        val intent = Intent().apply {
            action = getPackageName(context) + ".CRON"
            putExtra(Cron::class.java.simpleName, cron.id)
        }
        return if (!isForeground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "PendingIntent.getForegroundService....")
            PendingIntent.getForegroundService(context, cron.id.toInt(), intent, 0)
        } else {
            Log.d(TAG, "PendingIntent.getService....")
            return PendingIntent.getService(context, cron.id.toInt(), intent, 0)
        }
    }

    private fun getPackageName(context: Context) = context.packageManager.getPackageInfo(context.packageName, 0).packageName

}