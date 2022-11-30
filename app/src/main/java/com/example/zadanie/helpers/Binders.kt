package com.example.zadanie.helpers

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.snackbar.Snackbar

@BindingAdapter(
    "showTextToast"
)
fun applyShowTextToast(
    view: View,
    message: Evento<String>?
) {
    message?.getContentIfNotHandled()?.let {
        Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
    }
}