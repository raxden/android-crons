package com.raxdenstudios.sample;

import android.app.Application;

import com.raxdenstudios.commons.util.Utils;
import com.raxdenstudios.cron.CronRealmModule;
import com.raxdenstudios.cron.data.realm.CronRealmMigration;

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

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name(Utils.getPackageName(this))
                .modules(Realm.getDefaultModule(), new CronRealmModule())
                .migration(new CronRealmMigration())
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
