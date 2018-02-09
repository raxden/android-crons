package com.raxdenstudios.cron.realm;

import android.content.Context;

import com.raxdenstudios.cron.data.GenericService;

import java.util.ArrayList;
import java.util.Date;
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
import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by agomez on 06/06/2016.
 */
public class GenericRealmServiceImpl<T extends RealmModel, ID> implements GenericService<T, ID> {

    private final Context context;
    private final Class<T> persistentClass;

    public GenericRealmServiceImpl(Context context, Class<T> persistentClass) {
        this.context = context;
        this.persistentClass = persistentClass;
    }

    @Override
    public Maybe<List<T>> getAll() {
        return Maybe.create(new MaybeOnSubscribe<List<T>>() {
            List<T> data = new ArrayList<>();
            @Override
            public void subscribe(final MaybeEmitter<List<T>> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        data = findAll(realm);
                    }
                });
                closeRealmQuietly(realm);
                if (data.isEmpty()) {
                    emitter.onComplete();
                } else {
                    emitter.onSuccess(data);
                }
            }
        });
    }

    @Override
    public Single<T> get(final ID id) {
        return Single.create(new SingleOnSubscribe<T>() {
            T data = null;
            @Override
            public void subscribe(@NonNull final SingleEmitter<T> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        data = findFirst(realm, id);
                    }
                });
                closeRealmQuietly(realm);
                if (data != null) {
                    emitter.onSuccess(data);
                } else {
                    emitter.onError(new Exception("Cron with " + id + " not found"));
                }
            }
        });
    }

    @Override
    public Single<T> save(final T object) {
        return Single.create(new SingleOnSubscribe<T>() {
            T data = null;
            @Override
            public void subscribe(@NonNull final SingleEmitter<T> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        data = copyToRealmOrUpdate(realm, object);
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

    @Override
    public Completable delete(final ID id) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        deleteFromRealm(realm, id);
                    }
                });
                emitter.onComplete();
                closeRealmQuietly(realm);
            }
        });
    }

    protected T findFirst(Realm realm, ID id) {
        T data = null;
        RealmQuery<T> query = createQuery(realm, persistentClass, id);
        if (query != null) {
            T managedData = query.findFirst();
            if (managedData != null) {
                data = realm.copyFromRealm(managedData);
            }
        }
        return data;
    }

    protected List<T> findAll(Realm realm) {
        List<T> data = new ArrayList<>();
        RealmResults<T> managedResults = realm.where(persistentClass).findAll();
        if (!managedResults.isEmpty()) {
            data = realm.copyFromRealm(managedResults);
        }
        return data;
    }

    protected T copyToRealmOrUpdate(Realm realm, T data) {
        T managedData = realm.copyToRealmOrUpdate(data);
        return realm.copyFromRealm(managedData);
    }

    protected void deleteFromRealm(Realm realm, ID id) {
        RealmQuery<T> query = createQuery(realm, persistentClass, id);
        if (query != null) {
            T managedData = query.findFirst();
            if (managedData != null) {
                ((RealmObject) managedData).deleteFromRealm();
            }
        }
    }

    protected void closeRealmQuietly(Realm realm) {
        if (!realm.isClosed()) realm.close();
    }

    private RealmQuery<T> createQuery(Realm realm, Class<T> persistenceClass, ID id) {
        String fieldName = getPrimaryKeyFieldName(realm, persistenceClass);
        if (fieldName != null) {
            if (id instanceof String) {
                return realm.where(persistenceClass).equalTo(fieldName, (String) id);
            } else if (id instanceof Byte) {
                return realm.where(persistenceClass).equalTo(fieldName, (Byte) id);
            } else if (id instanceof Short) {
                return realm.where(persistenceClass).equalTo(fieldName, (Short) id);
            } else if (id instanceof Integer) {
                return realm.where(persistenceClass).equalTo(fieldName, (Integer) id);
            } else if (id instanceof Long) {
                return realm.where(persistenceClass).equalTo(fieldName, (Long) id);
            } else if (id instanceof Double) {
                return realm.where(persistenceClass).equalTo(fieldName, (Double) id);
            } else if (id instanceof Float) {
                return realm.where(persistenceClass).equalTo(fieldName, (Float) id);
            } else if (id instanceof Boolean) {
                return realm.where(persistenceClass).equalTo(fieldName, (Boolean) id);
            } else if (id instanceof Date) {
                return realm.where(persistenceClass).equalTo(fieldName, (Date) id);
            }
        }
        return null;
    }

    private String getPrimaryKeyFieldName(Realm realm, Class<T> persistentClass) {
        return realm.getSchema().get(persistentClass.getSimpleName()).getPrimaryKey();
    }

}
