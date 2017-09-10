package com.mekhedov.sasha.exness

import android.app.Application
import com.mekhedov.sasha.exness.dagger.AppComponent
import com.mekhedov.sasha.exness.dagger.DaggerAppComponent
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Elena on 11.12.2016.
 */

class BaseApplication : Application()   {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().build()
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
    }
}

