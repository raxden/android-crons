package com.raxdenstudios.cron.data;

import com.raxdenstudios.cron.model.Cron;

import rx.Observable;

/**
 * Created by Raxden on 02/06/2016.
 */
public interface CronDAO {

    Observable<Cron> create(Cron cron);
    Observable<Cron> update(Cron cron);
    Observable<Cron> find(long cronId);
    Observable<Cron> remove(Cron cron);

}
