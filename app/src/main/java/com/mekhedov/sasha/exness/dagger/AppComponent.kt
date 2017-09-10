package com.mekhedov.sasha.exness.dagger

import com.mekhedov.sasha.exness.mvp.MainPresenter
import com.mekhedov.sasha.exness.mvp.`MainPresenter$$ViewStateProvider`
import com.mekhedov.sasha.exness.ui.MainActivity
import com.mekhedov.sasha.exness.ui.SettingsAdapter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ListModelModule::class))
interface AppComponent {
    fun inject(mainActivity : MainActivity)
    fun inject(mainPresenter: MainPresenter)
    fun inject(settingsAdapter: SettingsAdapter)
}
