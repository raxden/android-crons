package com.raxdenstudios.cron.data;

import com.raxdenstudios.cron.model.Cron;

import java.util.List;

import rx.Observable;

/**
 * Created by Raxden on 02/06/2016.
 */
public interface CronDAO {

    Observable<Cron> create(Cron cron);
    Observable<Cron> update(Cron cron);
    Observable<List<Cron>> updateAll(List<Cron> crons);
    Observable<Cron> find(long cronId);
    Observable<List<Cron>> findAll();
    Observable<Cron> remove(long cronId);
    Observable<List<Cron>> removeAll();

}
