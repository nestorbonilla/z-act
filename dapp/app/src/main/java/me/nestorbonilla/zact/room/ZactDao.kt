package me.nestorbonilla.zact.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.AttendeeModel
import me.nestorbonilla.zact.model.CreatorModel

@Dao
interface ZactDao {

    //Acts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAct(actModel: ActModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertActs(actModels: List<ActModel>)

    @Query("SELECT * FROM act WHERE id = :id LIMIT 1")
    fun getAct(id: Int): ActModel

    @Query("SELECT * FROM act")
    fun getActList(): List<ActModel>

    @get:Query("SELECT * FROM act ORDER BY id ASC")
    val allActs: LiveData<List<ActModel>>

    //Creators
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreator(creatorModel: CreatorModel)

    @Query("SELECT * FROM creator WHERE id = :id LIMIT 1")
    fun getCreator(id: Int): CreatorModel

    //Attendee
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttendee(attendeeModel: AttendeeModel)

    @Query("SELECT * FROM attendee WHERE id = :id LIMIT 1")
    fun getAttendee(id: Int): AttendeeModel

}