package me.nestorbonilla.zact.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendee")
data class AttendeeModel (
    @PrimaryKey
    var id: Int,
    var actsAttended: Int
)