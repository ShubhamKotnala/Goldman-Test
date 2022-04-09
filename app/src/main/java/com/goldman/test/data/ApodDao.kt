package com.goldman.test.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ApodDao {
    @Query("SELECT * FROM apod_list")
    suspend fun getApodData(): List<ApodResponse>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(response: ApodResponse)

    @Query("DELETE FROM apod_list")
    suspend fun deleteAll()

    @Query("DELETE FROM apod_list WHERE date = :date")
    suspend fun deleteItem(date: String)

    @Query("SELECT EXISTS(SELECT * FROM apod_list WHERE date = :date)")
    fun isDataExists(date : String) : Boolean
}