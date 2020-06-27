package me.nestorbonilla.zact.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.nestorbonilla.zact.model.ActModel

@Dao
interface ZactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAct(actModel: ActModel)

    @Query("SELECT * FROM Act")
    fun getActList(): List<ActModel>

    @get:Query("SELECT * FROM Act ORDER BY id ASC")
    val allActs: LiveData<List<ActModel>>
}