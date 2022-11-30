package com.example.zadanie.ui.widget.friendList

import com.example.zadanie.data.db.model.FriendItem

interface FriendsEvents {
    fun onFriendClick(friend: FriendItem)
}