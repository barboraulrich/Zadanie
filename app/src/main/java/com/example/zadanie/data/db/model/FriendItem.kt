package com.example.zadanie.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
class FriendItem (
    @PrimaryKey val id: String,
    val name: String,
    val bar_id: String?,
    val bar_name: String?,
//    val time: String,
//    val bar_lat: Double,
//    val bar_lon: Double
){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FriendItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (bar_id != other.bar_id) return false
        if (bar_name != other.bar_name) return false
//        if (time != other.time) return false
//        if (bar_lat != other.bar_lat) return false
//        if (bar_lon != other.bar_lon) return false

        return true
    }

    override fun toString(): String {
        return "FriendItem(id='$id', name='$name', bar_id='$bar_id', bar_name='$bar_name')"
    }


    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + bar_id.hashCode()
        result = 31 * result + bar_name.hashCode()
//        result = 31 * result + time.hashCode()
//        result = 31 * result + bar_lat.hashCode()
//        result = 31 * result + bar_lon.hashCode()
        return result
    }
}