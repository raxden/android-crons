package com.raxdenstudios.cron.data.factory;

import android.content.Context;
import android.content.SharedPreferences;

import com.raxdenstudios.cron.data.CronService;
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
        mCronService = new CronPreferencesServiceImpl(context);
    }

    public CronFactoryService(Context context, String name, int mode) {
        mCronService = new CronPreferencesServiceImpl(context, name, mode);
    }

    public CronFactoryService(SharedPreferences settings) {
        mCronService = new CronPreferencesServiceImpl(settings);
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
