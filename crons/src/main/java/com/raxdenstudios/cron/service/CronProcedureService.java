package com.raxdenstudios.cron.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.factory.CronFactoryService;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.utils.CronUtils;

import java.util.Calendar;

import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class CronProcedureService extends Service {

    private static final String TAG = CronProcedureService.class.getSimpleName();

	private CronService mCronService;
	private CronHandler mCronHandler;

    @Override
    public void onCreate() {
    	super.onCreate();
        mCronService = new CronFactoryService(this);
        mCronHandler = new CronHandler(this, mCronService);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);

        Single.just(intent)
                .map(new Function<Intent, Long>() {
                    @Override
                    public Long apply(Intent intent) throws Exception {
                        return getCronIdFromIntent(intent);
                    }
                })
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long cronId) throws Exception {
                        return cronId != 0;
                    }
                })
                .flatMap(new Function<Long, MaybeSource<Cron>>() {
                    @Override
                    public MaybeSource<Cron> apply(Long cronId) throws Exception {
                        return mCronService.get(cronId).toMaybe();
                    }
                })
                .filter(new Predicate<Cron>() {
                    @Override
                    public boolean test(Cron cron) throws Exception {
                        return cron.isStatus();
                    }
                })
                .flatMap(new Function<Cron, MaybeSource<Cron>>() {
                    @Override
                    public MaybeSource<Cron> apply(Cron cron) throws Exception {
                        Log.d(TAG, "Cron[" + cron.getId() + "] launched at " + CronUtils.currentDateTime());
                        if (cron.getInterval() > 0) {
                            cron.setTriggerAtTime((Calendar.getInstance().getTimeInMillis() + cron.getInterval()));
                            return mCronHandler.start(cron).toSingleDefault(cron).toMaybe();
                        } else {
                            return Single.just(cron).toMaybe();
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Cron>() {
                    @Override
                    public void onSuccess(Cron cron) {
                        onCronLaunched(cron);
                        dispose();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                        dispose();
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });

        return START_STICKY;
    }

	protected abstract void onCronLaunched(Cron cron);

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    protected final IBinder mBinder = new Binder() {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    private long getCronIdFromIntent(Intent intent) {
        long cronId = 0;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey(Cron.class.getSimpleName())) {
                try {
                    cronId = extras.getLong(Cron.class.getSimpleName());
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
        return cronId;
    }
}
