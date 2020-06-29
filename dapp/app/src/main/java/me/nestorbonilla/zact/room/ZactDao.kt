package me.nestorbonilla.zact.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.ContactModel
import me.nestorbonilla.zact.model.GroupModel

@Dao
interface ZactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAct(actModel: ActModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contactModel: ContactModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(groupModel: GroupModel)

    @Query("SELECT * FROM act")
    fun getActList(): List<ActModel>

    @Query("SELECT * FROM contact")
    fun getContactList(): List<ContactModel>

    @Query("SELECT * FROM contact")
    fun getGroupList(): List<GroupModel>

    @get:Query("SELECT * FROM act ORDER BY id ASC")
    val allActs: LiveData<List<ActModel>>

    @get:Query("SELECT * FROM contact ORDER BY id ASC")
    val allContacts: LiveData<List<ContactModel>>

    @get:Query("SELECT * FROM contact_group ORDER BY id ASC")
    val allGroups: LiveData<List<GroupModel>>
}