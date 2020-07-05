package me.nestorbonilla.zact.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "act")
data class ActModel (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var _id: String,
    var fromAddress: String,
    var actAddress: String,             // only local
    var seed: String,                   // full seed only local
    var title: String,
    var publicInformation: String,
    var meetingPointRadius: Int,
    var meetingPoint: String
)