package com.raxdenstudios.cron.data.realm;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by agomez on 09/06/2016.
 */
public class CronRealmMigration implements RealmMigration {

    private static final String TAG = CronRealmMigration.class.getSimpleName();

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.d(TAG, "migrate from "+oldVersion+" to "+newVersion);

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
    }

}
