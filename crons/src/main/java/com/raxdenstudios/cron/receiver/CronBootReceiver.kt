package com.raxdenstudios.cron.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.raxdenstudios.cron.CronHandler
import io.reactivex.observers.DisposableCompletableObserver

class CronBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CronHandler(context).start()
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        dispose()
                    }

                    override fun onError(e: Throwable) {
                        dispose()
                    }
                })
    }

}
