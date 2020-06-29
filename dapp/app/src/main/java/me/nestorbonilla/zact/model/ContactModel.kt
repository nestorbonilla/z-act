package me.nestorbonilla.zact.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val groupId: Int,
    val name: String,
    val address: String
)