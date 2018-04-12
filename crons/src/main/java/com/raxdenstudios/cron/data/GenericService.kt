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
package com.raxdenstudios.cron.data

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface GenericService<T, ID> {

    /**
     * Gets the list of all objects of this type
     * @return list of this objects; the list can be empty
     */
    fun getAll(): Maybe<List<T>>

    /**
     * Gets the object using specified primary key
     * @param id primary key (ID) of the object to retrieve
     * @return object corresponding to the specified primary key (ID)
     */
    fun get(id: ID): Single<T>

    /**
     * Saves the specified object. Handles both insert as well as update
     * @param object the object to save
     * @return managed copy of the original object. Use this object if you want
     * the future changes managed (persisted).
     */
    fun save(data: T): Single<T>

    /**
     * Deletes the object that corresponds to the specified primary key (ID)
     * @param id primary key (ID) of the object to delete
     */
    fun delete(id: ID): Completable

}
