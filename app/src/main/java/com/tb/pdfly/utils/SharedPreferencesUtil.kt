package com.tb.pdfly.utils

import android.content.Context
import androidx.core.content.edit
import com.tb.pdfly.parameter.app
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


private val sharedPreferences by lazy {
    app.getSharedPreferences("pdfly_shared_prefs", Context.MODE_PRIVATE)
}

class PrefBoolean(private val default: Boolean) : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getBoolean(property.name, default)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        sharedPreferences.edit(commit = true) { putBoolean(property.name, value) }
    }
}

class PrefInt(private val default: Int) : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getInt(property.name, default)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        sharedPreferences.edit(commit = true) { putInt(property.name, value) }
    }
}

class PrefLong(private val default: Long) : ReadWriteProperty<Any?, Long> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getLong(property.name, default)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        sharedPreferences.edit(commit = true) { putLong(property.name, value) }
    }
}

class PrefString(private val default: String? = null) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.getString(property.name, default) ?: default ?: ""
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit(commit = true) { putString(property.name, value) }
    }
}

class PrefDouble(private val default: Double = 0.0) : ReadWriteProperty<Any?, Double> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return sharedPreferences.getString(property.name, default.toString())?.toDoubleOrNull() ?: default
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        sharedPreferences.edit(commit = true) { putString(property.name, value.toString()) }
    }
}

var isFirstAskStorage by PrefBoolean(true)