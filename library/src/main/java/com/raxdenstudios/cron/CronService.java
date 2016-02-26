package com.raxdenstudios.cron;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.util.CronUtils;

import java.util.Calendar;

import io.realm.Realm;

public abstract class CronService extends Service {

    private static final String TAG = CronService.class.getSimpleName();

	private Realm mRealm;

    @Override
    public void onCreate() {
    	super.onCreate();

		mRealm = Realm.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);

		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(Cron.class.getSimpleName())) {
            long cronId = intent.getExtras().getLong(Cron.class.getSimpleName());
            if (cronId > 0) {
                Cron cron = mRealm.where(Cron.class).equalTo("id", cronId).findFirst();
                if (cron != null) {
                    mRealm.beginTransaction();
                    cron.setTriggerAtTime((Calendar.getInstance().getTimeInMillis() + cron.getInterval()));
                    mRealm.commitTransaction();
                    if (cron.isStatus()) {
                        Log.d(TAG, "[onCronLaunched] " + CronUtils.dump(cron));
                        onCronLaunched(cron);
                    }
                }
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
