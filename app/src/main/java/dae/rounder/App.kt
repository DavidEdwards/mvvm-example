package dae.rounder

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.android.startKoin

@Suppress("unused")
class App: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        AndroidThreeTen.init(this)
    }

}