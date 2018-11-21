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
import com.google.gson.Gson
import com.raxdenstudios.cron.data.CronService
import com.raxdenstudios.cron.model.Cron
import com.raxdenstudios.preferences.AdvancedPreferences
import io.reactivex.*
import java.util.*
import kotlin.reflect.KClass

class CronPreferencesServiceImpl : CronService {

    private val persistentClass: KClass<Cron>
    private val advancedPreferences: AdvancedPreferences

    constructor(context: Context, gson: Gson = Gson()) {
        persistentClass = Cron::class
        advancedPreferences = AdvancedPreferences(context, gson)
    }

    constructor(context: Context, name: String, mode: Int, gson: Gson = Gson()) {
        persistentClass = Cron::class
        advancedPreferences = AdvancedPreferences(context, name, mode, gson)
    }

    constructor(preferences: SharedPreferences, gson: Gson = Gson()) {
        persistentClass = Cron::class
        advancedPreferences = AdvancedPreferences(preferences, gson)
    }

    override fun getAll(): Maybe<List<Cron>> {
        return Maybe.create { emitter: MaybeEmitter<List<Cron>> ->
            try {
                val cronList = getCronListFromPreferences()
                if (cronList.isNotEmpty())
                    emitter.onSuccess(cronList)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun get(id: Long): Single<Cron> {
        return Single.create { emitter: SingleEmitter<Cron> ->
            try {
                getCronFromPreferences(id)?.let { cron -> emitter.onSuccess(cron) }
                        ?: throw Exception("Cron with $id not found")
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun save(cron: Cron): Single<Cron> {
        return Single.create<Cron> { emitter ->
            try {
                saveCronToPreferences(cron)?.let { cron -> emitter.onSuccess(cron) }
                        ?: throw Exception("Cron with $cron.id could not save")
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun delete(id: Long): Completable {
        return Completable.create { emitter ->
            try {
                if (deleteCronFromPreferences(id))
                    emitter.onComplete()
                else
                    throw Exception("Cron with $id not found")
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    private fun getCronListFromPreferences(): List<Cron> {
        val cronList = ArrayList<Cron>()
        for (key in advancedPreferences.getAll().keys) {
            if (key.contains(persistentClass.java.simpleName.plus("_"))) {
                cronList.add(advancedPreferences.get(key, persistentClass.java, Cron.Builder(0).create()))
            }
        }
        return cronList.sortedBy { cron -> cron.id }
    }

    private fun getCronFromPreferences(id: Long): Cron? {
        val key = getKey(id)
        if (advancedPreferences.contains(key))
            return advancedPreferences.get(key, persistentClass.java, Cron.Builder(0).create())
        return null
    }

    private fun saveCronToPreferences(cron: Cron): Cron? {
        increasePrimaryKey(cron)
        advancedPreferences.put(getKey(cron.id), cron)
        advancedPreferences.commit()
        return cron
    }

    private fun deleteCronFromPreferences(id: Long): Boolean {
        val key = getKey(id)
        if (advancedPreferences.contains(key)) {
            advancedPreferences.remove(key)
            advancedPreferences.commit()
            return true
        }
        return false
    }

    private fun getKey(id: Long): String {
        return persistentClass.java.simpleName.plus("_$id")
    }

    private fun increasePrimaryKey(cron: Cron) {
        if (cron.id == 0L) {
            cron.id = getCronListFromPreferences().lastOrNull()?.id?.plus(1) ?: 1
        }
    }

}
