package com.raxdenstudios.cron

import android.content.Context
import android.util.Log
import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.data.factory.CronFactoryService
import com.raxdenstudios.cron.model.Cron
import com.raxdenstudios.cron.utils.CronUtils
import io.reactivex.Completable

class CronKHandler {

    companion object {
        private val TAG = CronKHandler::class.java.simpleName
    }

    private var context: Context? = null
    private var cronService: CronService? = null

    constructor(context: Context) {
        this.context = context
        this.cronService = CronFactoryService(context)
    }

    constructor(context: Context, cronService: CronService) {
        this.context = context
        this.cronService = cronService
    }

    fun start(cron: Cron): Completable {
        return cronService!!.save(cron)
                .map {
                    CronUtils.setAlarmManager(context, it)
                    it
                }
                .doOnSuccess { cronStarted(it) }
                .toCompletable()
    }

    fun finish(cronId: Long): Completable {
        return cronService!!.get(cronId)
                .flatMap {
                    CronUtils.cancelAlarmManager(context, it)
                    cronService!!.delete(cronId).toSingleDefault(it)
                }
                .doOnSuccess { cronFinished(it) }
                .toCompletable()
    }

    private fun cronStarted(cron: Cron) {
        Log.d(TAG, "Cron[$cron.id] started at ${CronUtils.currentDateTime()} " +
                "with interval ${CronUtils.intervalInSeconds(cron)}. " +
                "Next launch in ${CronUtils.nextLaunchInSeconds(cron)} ${CronUtils.triggerAtTime(cron)}")
    }

    private fun cronFinished(cron: Cron) {
        Log.d(TAG, "Cron[$cron.id] finished at ${CronUtils.currentDateTime()}")
    }

}