package com.raxdenstudios.cron.data.factory;

import android.content.Context;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.realm.CronRealmServiceImpl;
import com.raxdenstudios.cron.model.Cron;

import java.util.List;

import rx.Observable;

/**
 * Created by agomez on 09/06/2016.
 */
public class CronFactoryService implements CronService {

    private CronService mCronService;

    public CronFactoryService(Context context) {
        mCronService = new CronRealmServiceImpl(context);
    }

    @Override
    public Observable<List<Cron>> getAll() {
        return mCronService.getAll();
    }

    @Override
    public Observable<Cron> getById(Long aLong) {
        return mCronService.getById(aLong);
    }

    @Override
    public Observable<Cron> save(Cron object) {
        return mCronService.save(object);
    }

    @Override
    public Observable<Cron> delete(Long aLong) {
        return mCronService.delete(aLong);
    }
}
