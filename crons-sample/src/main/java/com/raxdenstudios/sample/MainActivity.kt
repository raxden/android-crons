package com.raxdenstudios.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.raxdenstudios.cron.CronHandler
import com.raxdenstudios.cron.model.Cron
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val now = Calendar.getInstance().timeInMillis

        val cron = Cron.Builder(1).triggerAtTime(now + 10000).interval(10000).create()
        val cron2 = Cron.Builder(2).triggerAtTime(now + 30000).create()
        val cron3 = Cron.Builder(2).triggerAtTime(now + 60000).create()

        compositeDisposable.add(CronHandler(this).start(cron)
                .andThen(CronHandler(this).start(cron2))
                .andThen(CronHandler(this).start(cron3))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete!")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message, e)
                    }
                }))
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()

        super.onDestroy()
    }
}