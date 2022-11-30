package com.example.zadanie.ui.widget.nearbyBars

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zadanie.ui.viewmodels.data.NearbyBar

class NearbyBarsRecyclerView : RecyclerView {
    private lateinit var barsAdapter: NearbyBarsAdapter
    var events: NearbyBarsEvents? = null
    /**
     * Default constructor
     *
     * @param context context for the activity
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * Constructor for XML layout
     *
     * @param context activity context
     * @param attrs   xml attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        barsAdapter = NearbyBarsAdapter(object : NearbyBarsEvents {
            override fun onBarClick(nearbyBar: NearbyBar) {
                events?.onBarClick(nearbyBar)
            }

        })
        adapter = barsAdapter
    }
}

@BindingAdapter(value = ["nearbyBars"])
fun NearbyBarsRecyclerView.applyNearbyBars(
    bars: List<NearbyBar>
) {
    (adapter as NearbyBarsAdapter).items = bars
}