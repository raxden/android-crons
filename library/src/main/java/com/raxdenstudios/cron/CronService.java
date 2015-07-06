package com.raxdenstudios.cron;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.raxdenstudios.cron.db.CronManager;
import com.raxdenstudios.cron.db.CronOpenHelper;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.db.DBManager;

public abstract class CronService extends Service {

    private static final String TAG = CronService.class.getSimpleName();

	private CronManager cronManager;
	
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	CronOpenHelper oh = initCronOpenHelper(getApplicationContext());
    	cronManager = new CronManager(oh);
    }
    
    protected abstract CronOpenHelper initCronOpenHelper(Context context);
	    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	
		if (intent != null && intent.getExtras() != null && intent.getExtras().get(CronOpenHelper.CRON_ID) != null) {
			
			int cronId = intent.getExtras().getInt(CronOpenHelper.CRON_ID);
			Log.d(TAG, "onStartCommand cronId: " + cronId);
			
			if (cronId > 0 && cronManager != null) {
			
				cronManager.find(Integer.toString(cronId), new DBManager.DBFindCallbacks<Cron>() {

					@Override
					public void dataFound(Cron cron) {
						if (cron != null && cron.isStatus()) {
							onCronLaunched(cronManager.getOpenHelper(), cron);
						} else {
							stopSelf();
						}
					}
					
				});
				
			} else {
				stopSelf();
			}
			
		} else {
			stopSelf();
		}
        
        return START_STICKY;
    }
    
	protected abstract void onCronLaunched(SQLiteOpenHelper oh, Cron cron);
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (cronManager != null) {
			cronManager.close();
		}		
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

	public CronManager getCronManager() {
		return cronManager;
	}

}
