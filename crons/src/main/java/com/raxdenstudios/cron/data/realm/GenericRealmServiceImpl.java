package com.raxdenstudios.cron.data.realm;

import android.content.Context;
import android.util.Log;

import com.raxdenstudios.cron.data.GenericService;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by agomez on 06/06/2016.
 */
public class GenericRealmServiceImpl<T extends RealmModel, ID> implements GenericService<T, ID> {

    private static final String TAG = GenericRealmServiceImpl.class.getSimpleName();

    protected Context mContext;
    protected Class<T> mPersistentClass;

    public GenericRealmServiceImpl(Context context, Class<T> persistentClass) {
        mContext = context;
        mPersistentClass = persistentClass;
    }

    @Override
    public Observable<List<T>> getAll() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(final Subscriber<? super List<T>> observer) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            RealmResults<T> managedResults = realm.where(mPersistentClass).findAll();
                            Log.d(TAG, "==[" + managedResults.size() + " " + mPersistentClass.getSimpleName() + "'s founded]==");
                            observer.onNext(realm.copyFromRealm(managedResults));
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

    @Override
    public Observable<T> getById(final ID id) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> observer) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            T data = null;
                            RealmQuery<T> query = createQuery(realm, mPersistentClass, id);
                            if (query != null) {
                                T managedData = query.findFirst();
                                if (managedData != null) {
                                    data = realm.copyFromRealm(managedData);
                                    Log.d(TAG, "==[Cron founded]==");
                                    Log.d(TAG, data.toString());
                                }
                            }
                            observer.onNext(data);
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

    @Override
    public Observable<T> save(final T object) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> observer) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            T manageData = realm.copyToRealmOrUpdate(object);
                            Log.d(TAG, "==[" + mPersistentClass.getSimpleName() + " saved]==");
                            Log.d(TAG, object.toString());
                            observer.onNext(object);
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

    @Override
    public Observable<T> delete(final ID id) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> observer) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            T data = null;
                            RealmQuery<T> query = createQuery(realm, mPersistentClass, id);
                            if (query != null) {
                                T managedData = query.findFirst();
                                if (managedData != null) {
                                    Log.d(TAG, "==[" + mPersistentClass.getSimpleName() + " deleted]==");
                                    ((RealmObject) managedData).deleteFromRealm();
                                }
                            }
                            observer.onNext(data);
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
