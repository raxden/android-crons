package com.raxdenstudios.cron.data.factory;

import android.content.Context;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.data.realm.CronRealmServiceImpl;
import com.raxdenstudios.cron.model.Cron;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by agomez on 09/06/2016.
 */
public class CronFactoryService implements CronService {

    private CronService mCronService;

    public CronFactoryService(Context context) {
        mCronService = new CronRealmServiceImpl(context);
    }

    @Override
    public Maybe<List<Cron>> getAll() {
        return mCronService.getAll();
    }

    @Override
    public Single<Cron> get(Long cronID) {
        return mCronService.get(cronID);
    }

    @Override
    public Single<Cron> save(Cron cron) {
        return mCronService.save(cron);
    }

    @Override
    public Completable delete(Long cronID) {
        return mCronService.delete(cronID);
    }

}
