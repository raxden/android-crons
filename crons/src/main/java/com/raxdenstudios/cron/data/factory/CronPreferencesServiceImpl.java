package com.raxdenstudios.cron.data.factory;

import android.content.Context;

import com.raxdenstudios.cron.data.CronService;
import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.preferences.AdvancedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by Ángel Gómez on 09/02/2018.
 */

public class CronPreferencesServiceImpl implements CronService {

    private final Context context;
    private final Class<Cron> persistentClass;
    private final AdvancedPreferences advancedPreferences;

    public CronPreferencesServiceImpl(Context context) {
        this.context = context;
        this.persistentClass = Cron.class;
        this.advancedPreferences = new AdvancedPreferences(context);
    }

    @Override
    public Maybe<List<Cron>> getAll() {
        return Maybe.create(new MaybeOnSubscribe<List<Cron>>() {
            @Override
            public void subscribe(MaybeEmitter<List<Cron>> emitter) throws Exception {
                List<Cron> cronList = getAll(advancedPreferences);
                if (!cronList.isEmpty()) {
                    emitter.onSuccess(cronList);
                } else {
                    emitter.onComplete();
                }
            }
        });
    }

    @Override
    public Single<Cron> get(final Long id) {
        return Single.create(new SingleOnSubscribe<Cron>() {
            @Override
            public void subscribe(SingleEmitter<Cron> emitter) throws Exception {
                if (advancedPreferences.contains(getKey(id))) {
                    emitter.onSuccess(advancedPreferences.get(getKey(id), persistentClass, new Cron.Builder(0).create()));
                } else {
                    emitter.onError(new Exception("Cron with " + id + " not found"));
                }
            }
        });
    }

    @Override
    public Single<Cron> save(final Cron cron) {
        return Single.create(new SingleOnSubscribe<Cron>() {
            @Override
            public void subscribe(SingleEmitter<Cron> emitter) throws Exception {
                increasePrimaryKey(advancedPreferences, cron);
                advancedPreferences.put(getKey(cron.getId()), cron);
                advancedPreferences.commit();
                emitter.onSuccess(cron);
            }
        });
    }

    @Override
    public Completable delete(final Long id) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if (advancedPreferences.contains(getKey(id))) {
                    advancedPreferences.remove(getKey(id));
                    advancedPreferences.commit();
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception("Cron with " + id + " not found"));
                }
            }
        });
    }

    private String getKey(final Long id) {
        return persistentClass.getSimpleName() + "_" + Long.toString(id);
    }

    private List<Cron> getAll(AdvancedPreferences preferences) {
        List<Cron> cronList = new ArrayList<>();
        for (String key : preferences.getAll().keySet()) {
            if (key.contains(persistentClass.getSimpleName() + "_")) {
                cronList.add(preferences.get(key, persistentClass, new Cron.Builder(0).create()));
            }
        }
        Collections.sort(cronList, new Comparator<Cron>() {
            @Override
            public int compare(Cron o1, Cron o2) {
                if (o1.getId() > o2.getId()) {
                    return 1;
                } else if (o1.getId() < o2.getId()) {
                    return -1;
                }
                return 0;
            }
        });
        return cronList;
    }

    private void increasePrimaryKey(AdvancedPreferences preferences, Cron cron) {
        if (cron.getId() == 0) {
            long id = 1;
            List<Cron> crons = getAll(preferences);
            if (!crons.isEmpty()) {
                Cron lastCron = crons.get(crons.size() - 1);
                id = lastCron.getId() + 1;
            }
            cron.setId(id);
        }
    }

}
