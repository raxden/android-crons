package com.raxdenstudios.cron

import android.content.Context
import android.util.Log
import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.data.factory.CronFactoryService
import com.raxdenstudios.cron.model.Cron
import com.raxdenstudios.cron.utils.CronUtils
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*

class CronHandler(val context: Context, private val cronService: CronService = CronFactoryService(context)) {

    companion object {
        private val TAG = CronHandler::class.java.simpleName
    }

    fun start(cron: Cron): Completable {
        return cronService.save(cron)
                .map {
                    CronUtils.setAlarmManager(context, it)
                    it
                }
                .doOnSuccess { cronStarted(it) }
                .toCompletable()
    }

    fun finish(cronId: Long): Completable {
        return cronService.get(cronId)
                .flatMap {
                    CronUtils.cancelAlarmManager(context, it)
                    cronService.delete(cronId).toSingleDefault(it)
                }
                .doOnSuccess { cronFinished(it) }
                .toCompletable()
    }

    fun start(): Completable {
        return cronService.getAll()
                .flatMapCompletable {
                    updateTriggerAtTimeFromCronListAndPersist(it)
                }
    }

    fun finish(): Completable {
        return cronService.getAll()
                .flatMapCompletable {
                    removeCronListAndPersist(it)
                }
    }

    fun removeCronListAndPersist(cronList: List<Cron>) : Completable {
        val finishCronList = mutableListOf<Single<Cron>>()
        for (cron in cronList) {
            finishCronList.add(finish(cron.id).toSingleDefault(cron).subscribeOn(Schedulers.io()))
        }
        return Single.zip(finishCronList, Function<Array<Any>, Boolean> { true }).toCompletable()
    }

    fun updateTriggerAtTimeFromCronListAndPersist(cronList: List<Cron>): Completable {
        val now = Calendar.getInstance().timeInMillis
        for (cron in cronList) {
            var triggerAtTime = cron.triggerAtTime
            if (triggerAtTime < now) {
                do {
                    triggerAtTime = triggerAtTime + cron.interval
                } while (triggerAtTime < now)
                cron.triggerAtTime = triggerAtTime
            }
        }
        val startCronList = mutableListOf<Single<Cron>>()
        for (cron in cronList) {
            startCronList.add(start(cron).toSingleDefault(cron).subscribeOn(Schedulers.io()))
        }
        return Single.zip(startCronList, Function<Array<Any>, Boolean> { true }).toCompletable()
    }

    private fun cronStarted(cron: Cron) {
        Log.d(TAG, "Cron[${cron.id}] started at ${CronUtils.currentDateTime()} " +
                "with interval ${CronUtils.intervalInSeconds(cron)}. " +
                "Next launch in ${CronUtils.nextLaunchInSeconds(cron)} ${CronUtils.triggerAtTime(cron)}")
    }

    private fun cronFinished(cron: Cron) {
        Log.d(TAG, "Cron[${cron.id}] finished at ${CronUtils.currentDateTime()}")
    }

}