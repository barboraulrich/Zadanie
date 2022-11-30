package com.example.zadanie.ui.widget.barlist

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.ui.fragments.BarsFragmentDirections

class BarsRecyclerView : RecyclerView {
    private lateinit var barsAdapter: BarsAdapter
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
        barsAdapter = BarsAdapter(object : BarsEvents {
            override fun onBarClick(bar: BarItem) {
                this@BarsRecyclerView.findNavController().navigate(
                    BarsFragmentDirections.actionBarsFragmentToDetailFragment(bar.id)
                )
            }
        })
        adapter = barsAdapter
    }
}

@BindingAdapter(value = ["barItems"])
fun BarsRecyclerView.applyItems(
    bars: List<BarItem>?
) {
    (adapter as BarsAdapter).items = bars ?: emptyList()
}