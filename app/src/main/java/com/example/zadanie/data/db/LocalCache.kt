package com.example.zadanie.data.db

import androidx.lifecycle.LiveData
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem
import com.example.zadanie.ui.viewmodels.Sort
import com.example.zadanie.ui.viewmodels.data.MyLocation
import kotlinx.coroutines.flow.Flow

class LocalCache(private val dao: DbDao) {
    suspend fun insertBars(bars: List<BarItem>){
        dao.insertBars(bars)
    }

    suspend fun deleteBars(){ dao.deleteBars() }

    fun getBars(sort: Sort): LiveData<List<BarItem>?> {
        return when (sort) {
            Sort.GUESTS_ASCENDING -> {
                dao.getBarsGuestsAscending()
            }
            Sort.GUESTS_DESCENDING -> {
                dao.getBarsGuestsDescending()
            }
            Sort.DISTANCE_ASCENDING -> {
                //TODO
                dao.getBars()
            }
            Sort.DISTANCE_DESCENDING -> {
                //TODO
                dao.getBars()
            }
            Sort.TITLE_DESCENDING -> {
                dao.getBarsTitleDescending()
            }
            else -> {
                // default: TITLE_ASCENDING
                dao.getBarsTitleAscending()
            }
        }
    }
    fun getBarsSortedByDistance(sort: Sort, location: MyLocation): LiveData<List<BarItem>?>
    {
        return when(sort)
        {
            Sort.DISTANCE_ASCENDING -> {
                dao.getSortedBarsByDistanceASC(location.lat, location.lon)
            }
            Sort.DISTANCE_DESCENDING -> {
                dao.getSortedBarsByDistanceDESC(location.lat, location.lon)
            }
            else -> {dao.getBarsTitleAscending()}
        }
    }

    suspend fun insertFriends(friends: List<FriendItem>){
        dao.insertFriends(friends)
    }

    suspend fun deleteFriends(){ dao.deleteFriends() }

    fun getFriends(): LiveData<List<FriendItem>?> = dao.getFriends()

    fun getBarById(id: String): Flow<BarItem> {
        return dao.getBarById(id)
    }
}