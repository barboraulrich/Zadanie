package com.example.zadanie.ui.widget.barlist

import com.example.zadanie.data.db.model.BarItem

interface BarsEvents {
    fun onBarClick(bar: BarItem)
}