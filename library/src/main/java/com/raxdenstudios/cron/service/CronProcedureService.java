package com.raxdenstudios.cron.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.factory.CronFactoryService;
import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class CronProcedureService extends Service {

    private static final String TAG = CronProcedureService.class.getSimpleName();

	private CronService mCronService;

    @Override
    public void onCreate() {
    	super.onCreate();
        mCronService = new CronFactoryService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "CronProcedureService started....");

        long cronId = getCrongIdFromExtras(intent);
        if (cronId > 0) {
            mCronService.getById(cronId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Cron, Cron>() {
                        @Override
                        public Cron call(Cron cron) {
                            cron.setTriggerAtTime((Calendar.getInstance().getTimeInMillis() + cron.getInterval()));
                            return cron;
                        }
                    })
                    .concatMap(new Func1<Cron, Observable<Cron>>() {
                        @Override
                        public Observable<Cron> call(Cron cron) {
                            return mCronService.save(cron)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    })
                    .filter(new Func1<Cron, Boolean>() {
                        @Override
                        public Boolean call(Cron cron) {
                            return cron.isStatus();
                        }
                    })
                    .doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            stopSelf();
                        }
                    })
                    .subscribe(new Action1<Cron>() {
                        @Override
                        public void call(Cron cron) {
                            Log.d(TAG, "Cron launched! " + cron.toString());
                            onCronLaunched(cron);
                            Log.d(TAG, "CronProcedureService finished....");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable t) {
                            Log.e(TAG, t.getMessage(), t);
                            Log.d(TAG, "CronProcedureService finished....");
                        }
                    });
        }

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

    private long getCrongIdFromExtras(Intent intent) {
        long cronId = 0;
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(Cron.class.getSimpleName())) {
            try {
                cronId = intent.getExtras().getLong(Cron.class.getSimpleName());
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return cronId;
    }
}
