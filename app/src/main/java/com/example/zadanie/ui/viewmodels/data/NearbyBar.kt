package com.example.zadanie.ui.viewmodels.data

import android.location.Location

class NearbyBar(
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>,
    var distance: Double = 0.0
){

    fun distanceTo(location: MyLocation): Double{
        return Location("").apply {
            latitude=lat
            longitude=lon
        }.distanceTo(Location("").apply {
            latitude=location.lat
            longitude=location.lon
        }).toDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NearbyBar) return false

        if (type != other.type) return false
        if (id != other.id) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }

    override fun toString(): String {
        return "NearbyBar(type='$type', id=$id, lat=$lat, lon=$lon, tags=$tags)"
    }
}