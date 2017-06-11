package com.raxdenstudios.cron.data.realm;

import android.content.Context;
import android.util.Log;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.model.Cron;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by agomez on 06/06/2016.
 */
public class CronRealmServiceImpl extends GenericRealmServiceImpl<Cron, Long> implements CronService {

    private static final String TAG = CronRealmServiceImpl.class.getSimpleName();

    public CronRealmServiceImpl(Context context) {
        super(context, Cron.class);
    }

    @Override
    public Observable<Cron> save(final Cron cron) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(final Subscriber<? super Cron> observer) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            if (cron.getId() == 0) {
                                long id = 1;
                                if (realm.where(Cron.class).count() > 0) {
                                    Number number = realm.where(Cron.class).max("id");
                                    id = (number != null ? number.longValue() + 1 : 1);
                                }
                                cron.setId(id);
                            }
                            Cron manageData = realm.copyToRealmOrUpdate(cron);
                            Log.d(TAG, "==[Cron saved]== ");
                            Log.d(TAG, cron.toString());
                            observer.onNext(cron);
                            observer.onCompleted();
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
                if (!realm.isClosed()) realm.close();
            }
        });
    }

}
