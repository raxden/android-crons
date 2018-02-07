package com.raxdenstudios.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now = Calendar.getInstance().getTimeInMillis();

        final Cron cron = new Cron.Builder(1)
                .triggerAtTime(now + 10000)
                .interval(10000)
                .create();

        final Cron cron2 = new Cron.Builder(2)
                .triggerAtTime(now + 30000)
                .interval(30000)
                .create();

        final Cron cron3 = new Cron.Builder(3)
                .triggerAtTime(now + 60000)
                .interval(60000)
                .create();

        CronHandler handler = new CronHandler(this);

        mDisposable = handler.start(cron)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}
