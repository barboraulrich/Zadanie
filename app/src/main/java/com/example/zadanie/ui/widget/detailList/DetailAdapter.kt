package com.example.zadanie.ui.widget.detailList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zadanie.R
import com.example.zadanie.helpers.autoNotify
import kotlin.properties.Delegates

class DetailAdapter() : RecyclerView.Adapter<DetailAdapter.BarDetailItemViewHolder>() {
    var items: List<BarDetailItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.key.compareTo(n.key) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarDetailItemViewHolder {
        return BarDetailItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BarDetailItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class BarDetailItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.bar_detail_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BarDetailItem) {
            itemView.findViewById<TextView>(R.id.name).text = item.key
            itemView.findViewById<TextView>(R.id.value).text = item.value
        }
    }
}