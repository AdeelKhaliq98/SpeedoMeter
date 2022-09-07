package com.example.HistoryDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(insert_History: HistoryEntityDataClass)

    @Query("Select * from  HistoryList order by local_id asc ")
    fun getHistoryRecord(): LiveData<List<HistoryEntityDataClass>>

    @Delete
     fun  deleteHistory(local_id :HistoryEntityDataClass)

    @Query("SELECT * FROM HistoryList WHERE local_id = :local_id")
    fun loadTaskById(local_id:Int): LiveData<HistoryEntityDataClass?>

}