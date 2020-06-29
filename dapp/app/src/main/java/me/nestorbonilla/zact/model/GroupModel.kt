package me.nestorbonilla.zact.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_group")
class GroupModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)