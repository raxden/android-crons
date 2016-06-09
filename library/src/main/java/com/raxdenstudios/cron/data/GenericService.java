package com.raxdenstudios.cron.data;

import java.util.List;

import rx.Observable;

/**
 * Created by agomez on 06/06/2016.
 */
public interface GenericService<T, ID> {

    /**
     * Gets the list of all objects of this type
     * @return list of this objects; the list can be empty
     */
    Observable<List<T>> getAll();

    /**
     * Gets the object using specified primary key
     * @param id primary key (ID) of the object to retrieve
     * @return object corresponding to the specified primary key (ID)
     */
    Observable<T> getById(ID id);

    /**
     * Saves the specified object. Handles both insert as well as update
     * @param object the object to save
     * @return managed copy of the original object. Use this object if you want
     *          the future changes managed (persisted).
     */
    Observable<T> save(T object);

    /**
     * Deletes the object that corresponds to the specified primary key (ID)
     * @param id primary key (ID) of the object to delete
     */
    Observable<T> delete(ID id);

}
