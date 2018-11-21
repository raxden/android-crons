package com.raxdenstudios.cron.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import com.raxdenstudios.cron.CronHandler
import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.data.factory.CronFactoryService
import com.raxdenstudios.cron.model.Cron
import com.raxdenstudios.cron.utils.CronUtils
import io.reactivex.Maybe
import io.reactivex.observers.DisposableMaybeObserver
import java.util.*

abstract class CronProcedureService : Service() {

    private val cronService: CronService by lazy {
        CronFactoryService(this)
    }

    private val cronHandler: CronHandler by lazy {
        CronHandler(this, cronService)
    }

    companion object {
        private val TAG = CronProcedureService::class.java.simpleName
    }

    abstract fun onCronLaunched(cron: Cron)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Maybe.just(intent)
                .map { intent?.extras?.getLong(Cron::class.java.simpleName) }
                .filter { it != 0L }
                .flatMap { cronService.get(it!!).toMaybe() }
                .filter{ it.status }
                .flatMap {
                    Log.d(TAG, "Cron[${it.id}] launched at ${CronUtils.currentDateTime()}")
                    if (it.interval > 0) {
                        it.triggerAtTime = Calendar.getInstance().timeInMillis + it.interval
                        cronHandler.start(it).toSingleDefault(it).toMaybe()
                    } else {
                        Maybe.just(it)
                    }
                }
                .subscribeWith(object : DisposableMaybeObserver<Cron>() {
                    override fun onSuccess(t: Cron) {
                        onCronLaunched(t)
                        dispose()
                    }

                    override fun onComplete() {
                        dispose()
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message, e)
                        dispose()
                    }
                })

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : Binder() {
            @Throws(RemoteException::class)
            override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
                return super.onTransact(code, data, reply, flags)
            }
        }
    }

}