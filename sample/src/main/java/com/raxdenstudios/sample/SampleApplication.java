package com.raxdenstudios.sample;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by agomez on 26/02/2016.
 */
public class SampleApplication extends Application {

    private static final String TAG = SampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
