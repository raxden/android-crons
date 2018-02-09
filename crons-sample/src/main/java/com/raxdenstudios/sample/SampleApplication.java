package com.raxdenstudios.sample;

import android.app.Application;

import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.realm.CronRealmMigration;
import com.raxdenstudios.cron.realm.CronRealmModule;

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

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Utils.getPackageName(this))
                .modules(Realm.getDefaultModule(), new CronRealmModule())
                .migration(new CronRealmMigration())
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
