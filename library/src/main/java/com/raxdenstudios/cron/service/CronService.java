package com.raxdenstudios.cron.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.raxdenstudios.cron.data.CronDAO;
import com.raxdenstudios.cron.data.CronDAOImpl;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.util.CronUtils;

import java.util.Calendar;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class CronService extends Service {

    private static final String TAG = CronService.class.getSimpleName();

    private Realm mRealm;
	private CronDAO mCronDAO;

    @Override
    public void onCreate() {
    	super.onCreate();

        mRealm = Realm.getDefaultInstance();
        mCronDAO = new CronDAOImpl(this, mRealm);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);

		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(Cron.class.getSimpleName())) {
            long cronId = intent.getExtras().getLong(Cron.class.getSimpleName());
            if (cronId > 0) {
                mCronDAO.find(cronId)
                        .subscribeOn(Schedulers.immediate())
                        .observeOn(Schedulers.immediate())
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
                                return mCronDAO.update(cron);
                            }
                        })
                        .filter(new Func1<Cron, Boolean>() {
                            @Override
                            public Boolean call(Cron cron) {
                                return cron.isStatus();
                            }
                        })
                        .subscribe(new Action1<Cron>() {
                            @Override
                            public void call(Cron cron) {
                                Log.d(TAG, "[onCronLaunched] " + CronUtils.dump(cron));
                                onCronLaunched(cron);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t) {
                                Log.e(TAG, t.getMessage(), t);
                            }
                        });
            }
		}
        return START_STICKY;
    }
    
	protected abstract void onCronLaunched(Cron cron);
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (mRealm != null) {
			mRealm.close();
		}		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public Realm getRealm() {
		return mRealm;
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

}
