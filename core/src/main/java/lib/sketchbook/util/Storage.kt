package lib.sketchbook.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
fun rememberSharedPrefs(name: String): SharedPreferences {
    val context = LocalContext.current
    val view = LocalView.current
    return remember(context, name) {
        if (view.isInEditMode) MockPrefs
        else context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}

fun SharedPreferences.getLongArray(key: String, default: LongArray): LongArray {
    return getString(key, null)?.split(',')?.map { it.toLong() }?.toLongArray() ?: default
}

fun SharedPreferences.Editor.putLongArray(key: String, value: LongArray) {
    putString(key, value.joinToString(",") { it.toString() })
}

private object MockPrefs : SharedPreferences {
    override fun getAll(): Map<String, *> = emptyMap<String, Any>()

    override fun getString(key: String?, defValue: String?) = defValue

    override fun getStringSet(key: String?, defValues: Set<String>?) = defValues

    override fun getInt(key: String?, defValue: Int) = defValue

    override fun getLong(key: String?, defValue: Long) = defValue

    override fun getFloat(key: String?, defValue: Float) = defValue

    override fun getBoolean(key: String?, defValue: Boolean) = defValue

    override fun contains(key: String?) = false

    override fun edit(): SharedPreferences.Editor = MockEditor

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
}

private object MockEditor : SharedPreferences.Editor {
    override fun putString(key: String?, value: String?) = this

    override fun putStringSet(key: String?, values: Set<String>?) = this

    override fun putInt(key: String?, value: Int) = this

    override fun putLong(key: String?, value: Long) = this

    override fun putFloat(key: String?, value: Float) = this

    override fun putBoolean(key: String?, value: Boolean) = this

    override fun remove(key: String?) = this

    override fun clear() = this

    override fun commit() = true

    override fun apply() {}
}
