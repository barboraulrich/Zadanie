package com.example.zadanie.ui.widget.friendList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zadanie.R
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem
import com.example.zadanie.helpers.autoNotify
import com.google.android.material.chip.Chip
import kotlin.properties.Delegates

class FriendsAdapter(val events: FriendsEvents? = null) :
    RecyclerView.Adapter<FriendsAdapter.FriendItemViewHolder>() {
    var items: List<FriendItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendItemViewHolder {
        return FriendItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FriendItemViewHolder, position: Int) {
        holder.bind(items[position], events)
    }

    class FriendItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.friend_item,
            parent,
            false)
        ) : RecyclerView.ViewHolder(itemView){

        fun bind(item: FriendItem, events: FriendsEvents?) {
            itemView.findViewById<TextView>(R.id.name).text = item.name
            itemView.findViewById<TextView>(R.id.bar_name).text = item.bar_name

            itemView.setOnClickListener { events?.onFriendClick(item) }
        }
    }
}