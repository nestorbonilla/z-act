package me.nestorbonilla.zact.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val address: String
)