package com.raxdenstudios.cron.data;

import android.content.Context;
import android.util.Log;

import com.raxdenstudios.cron.exception.ResultsNotFoundException;
import com.raxdenstudios.cron.model.Cron;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Raxden on 02/06/2016.
 */
public class CronDAOImpl implements CronDAO {

    private static final String TAG = CronDAOImpl.class.getSimpleName();

    private Context mContext;
    private Realm mRealm;

    public CronDAOImpl(Context context) {
        this(context, Realm.getDefaultInstance());
    }

    public CronDAOImpl(Context context, Realm realm) {
        mContext = context;
        mRealm = realm;
    }

    @Override
    public Observable<Cron> create(final Cron cron) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(final Subscriber<? super Cron> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
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
                            Cron managedCron = realm.copyToRealmOrUpdate(cron);
                            Log.d(TAG, "==[Cron created]["+managedCron.getId()+"]===================");
                            observer.onNext(realm.copyFromRealm(managedCron));
                            observer.onCompleted();
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<Cron> update(final Cron cron) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(final Subscriber<? super Cron> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            Cron managedCron = realm.where(Cron.class).equalTo("id", cron.getId()).findFirst();
                            if (managedCron != null) {
                                managedCron = realm.copyToRealmOrUpdate(cron);
                                Log.d(TAG, "==[Cron updated]["+managedCron.getId()+"]===================");
                                observer.onNext(realm.copyFromRealm(managedCron));
                                observer.onCompleted();
                            } else {
                                observer.onError(new ResultsNotFoundException());
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<List<Cron>> updateAll(final List<Cron> crons) {
        return Observable.create(new Observable.OnSubscribe<List<Cron>>() {
            @Override
            public void call(final Subscriber<? super List<Cron>> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            List<Cron> managedCrons = new ArrayList<>();
                            for (Cron cron : crons) {
                                Cron managedCron = realm.where(Cron.class).equalTo("id", cron.getId()).findFirst();
                                if (managedCron != null) {
                                    managedCron = realm.copyToRealmOrUpdate(cron);
                                    Log.d(TAG, "==[Cron updated]["+managedCron.getId()+"]===================");
                                    managedCrons.add(managedCron);
                                } else {

                                }
                            }
                            observer.onNext(realm.copyFromRealm(managedCrons));
                            observer.onCompleted();
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<Cron> find(final long cronId) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(final Subscriber<? super Cron> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            Cron managedCron = realm.where(Cron.class).equalTo("id", cronId).findFirst();
                            if (managedCron != null) {
                                Log.d(TAG, "==[Cron founded]["+managedCron.getId()+"]===================");
                                observer.onNext(realm.copyFromRealm(managedCron));
                                observer.onCompleted();
                            } else {
                                observer.onError(new ResultsNotFoundException());
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<List<Cron>> findAll() {
        return Observable.create(new Observable.OnSubscribe<List<Cron>>() {
            @Override
            public void call(final Subscriber<? super List<Cron>> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            RealmResults<Cron> managedCrons = realm.where(Cron.class).findAll();
                            if (!managedCrons.isEmpty()) {
                                Log.d(TAG, "==[Crons founded]["+managedCrons.size()+"]===================");
                                observer.onNext(realm.copyFromRealm(managedCrons));
                                observer.onCompleted();
                            } else {
                                observer.onError(new ResultsNotFoundException());
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<Cron> remove(final long cronId) {
        return Observable.create(new Observable.OnSubscribe<Cron>() {
            @Override
            public void call(final Subscriber<? super Cron> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            Cron managedCron = realm.where(Cron.class).equalTo("id", cronId).findFirst();
                            if (managedCron != null) {
                                Log.d(TAG, "==[Cron removed]["+managedCron.getId()+"]===================");
                                observer.onNext(realm.copyFromRealm(managedCron));
                                managedCron.deleteFromRealm();
                                observer.onCompleted();
                            } else {
                                observer.onError(new ResultsNotFoundException());
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<List<Cron>> removeAll() {
        return Observable.create(new Observable.OnSubscribe<List<Cron>>() {
            @Override
            public void call(final Subscriber<? super List<Cron>> observer) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            RealmResults<Cron> managedCrons = realm.where(Cron.class).findAll();
                            if (!managedCrons.isEmpty()) {
                                Log.d(TAG, "==[Crons removed]["+managedCrons.size()+"]===================");
                                observer.onNext(realm.copyFromRealm(managedCrons));
                                managedCrons.deleteAllFromRealm();
                                observer.onCompleted();
                            } else {
                                observer.onError(new ResultsNotFoundException());
                            }
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, e.getMessage(), e);
                            observer.onError(e);
                        }
                    }
                });
            }
        });
    }
}
