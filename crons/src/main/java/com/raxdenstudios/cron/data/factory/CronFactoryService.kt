package com.raxdenstudios.cron.data.factory

import android.content.Context
import android.content.SharedPreferences

import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.model.Cron

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by agomez on 09/06/2016.
 */
class CronFactoryService : CronService {

    private var mCronService: CronService? = null

    constructor(context: Context) {
        mCronService = CronPreferencesServiceImpl(context)
    }

    constructor(context: Context, name: String, mode: Int) {
        mCronService = CronPreferencesServiceImpl(context, name, mode)
    }

    constructor(settings: SharedPreferences) {
        mCronService = CronPreferencesServiceImpl(settings)
    }

    override fun getAll(): Maybe<List<Cron>> {
        return mCronService!!.all
    }

    override fun get(cronID: Long?): Single<Cron> {
        return mCronService!!.get(cronID)
    }

    override fun save(cron: Cron): Single<Cron> {
        return mCronService!!.save(cron)
    }

    override fun delete(cronID: Long?): Completable {
        return mCronService!!.delete(cronID)
    }

}
