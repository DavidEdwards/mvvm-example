package dae.rounder.utils

import android.util.Log
import dae.rounder.BuildConfig

@Suppress("unused")
object LogUtils {
    private const val LOG_TAG = "RDR"

    @JvmStatic
    fun log(data: String) {
        log("APP", data)
    }

    @JvmStatic
    fun log(tag: String, data: String) {
        if (!BuildConfig.DEBUG) return

        val element = Thread.currentThread().stackTrace[4]
        val from = element.className.subSequence(element.className.lastIndexOf(".") + 1, element.className.length) as String
        val lineNumber = element.lineNumber

        when {
            data.startsWith("V::") -> Log.v("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data.substring(3)))
            data.startsWith("E::") -> Log.e("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data.substring(3)))
            data.startsWith("I::") -> Log.i("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data.substring(3)))
            data.startsWith("W::") -> Log.w("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data.substring(3)))
            data.startsWith("WTF::") -> Log.wtf("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data.substring(3)))
            else -> {
                Log.v("$LOG_TAG.$tag", String.format("%s#%s:%s", from, lineNumber, data))
            }
        }
    }
}
