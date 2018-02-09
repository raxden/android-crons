package com.raxdenstudios.cron.realm;

import android.content.Context;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.model.Cron;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.realm.Realm;

/**
 * Created by agomez on 06/06/2016.
 */
public class CronRealmServiceImpl extends GenericRealmServiceImpl<Cron, Long> implements CronService {

    public CronRealmServiceImpl(Context context) {
        super(context, Cron.class);
    }

    @Override
    public Single<Cron> save(final Cron cron) {
        return Single.create(new SingleOnSubscribe<Cron>() {
            Cron data = null;
            @Override
            public void subscribe(@NonNull final SingleEmitter<Cron> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        increasePrimaryKey(realm, cron);
                        data = copyToRealmOrUpdate(realm, cron);
                    }
                });
                closeRealmQuietly(realm);
                if (data != null) {
                    emitter.onSuccess(data);
                } else {
                    emitter.onError(new Exception("The cron could not be saved!"));
                }
            }
        });
    }

    private void increasePrimaryKey(Realm realm, Cron cron) {
        if (cron.getId() == 0) {
            long id = 1;
            if (realm.where(Cron.class).count() > 0) {
                Number number = realm.where(Cron.class).max("id");
                id = (number != null ? number.longValue() + 1 : 1);
            }
            cron.setId(id);
        }
    }

}
