package me.nestorbonilla.zact.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "act")
data class ActModel (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var description: String
)