package com.raxdenstudios.cron.realm;

import com.raxdenstudios.cron.model.Cron;

import io.realm.annotations.RealmModule;

/**
 * Created by agomez on 29/02/2016.
 */
@RealmModule(library = true, classes = {Cron.class})
public class CronRealmModule {

}
