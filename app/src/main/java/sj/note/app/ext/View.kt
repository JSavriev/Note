package sj.note.app.ext

import android.app.Activity
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Activity.inflate(@LayoutRes layoutRes: Int): View {
    return layoutInflater.inflate(layoutRes, null)
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}