package com.hapley.pocketqr.common

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * Created by Fernando Fransisco Halim on 2020-01-23.
 */

interface BaseDao<T> {

    @Insert
    suspend fun insert(obj: T)

    @Insert
    suspend fun insert(obj: List<T>)

    @Update
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T)
}
