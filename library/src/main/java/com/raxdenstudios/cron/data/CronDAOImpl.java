package com.raxdenstudios.cron.data;

import android.content.Context;

import com.raxdenstudios.cron.model.Cron;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Raxden on 02/06/2016.
 */
public class CronDAOImpl implements CronDAO {

    private Context mContext;

    public CronDAOImpl(Context context) {
        mContext = context;
    }

    @Override
    public Observable<Cron> create(Cron cron) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(Subscriber<? super Cron> observer) {
                observer.onNext();
            }
        });
    }

    @Override
    public Observable<Cron> update(Cron cron) {
        return null;
    }

    @Override
    public Observable<Cron> find(long cronId) {
        return null;
    }

    @Override
    public Observable<Cron> remove(Cron cron) {
        return null;
    }

}
