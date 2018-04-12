/*
 * Copyright 2014 Ángel Gómez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raxdenstudios.cron.data.factory

import android.content.Context
import android.content.SharedPreferences

import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.model.Cron

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

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
