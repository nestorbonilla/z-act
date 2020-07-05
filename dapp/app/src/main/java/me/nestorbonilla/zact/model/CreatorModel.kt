package me.nestorbonilla.zact.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "creator")
data class CreatorModel (
    @PrimaryKey
    var id: Int,
    var seed: String,
    var address: String,
    var actsCreated: Int
)