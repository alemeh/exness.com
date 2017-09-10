package com.mekhedov.sasha.exness.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import com.mekhedov.sasha.exness.R
import com.mekhedov.sasha.exness.mvp.MainModel
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.querySorted
import com.vicpin.krealmextensions.rx.queryAllAsObservable
import io.realm.Sort
import kotlinx.android.synthetic.main.dialog_settings.*

/**
 * Created by Elena on 10.09.2017.
 */
class SettingsDialog(context: Context) : Dialog(context) {
    lateinit var adapter: SettingsAdapter
    val list = ArrayList<MainModel>()

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_settings)

        adapter = SettingsAdapter(context, list)
        rvSettings.adapter = adapter
        rvSettings.layoutManager = LinearLayoutManager(context)

        val modelObserver = MainModel().queryAllAsObservable()
        modelObserver.asObservable().subscribe {
            list.clear()
            list.addAll(MainModel().querySorted("sort", Sort.ASCENDING))
        }

        btnOk.setOnClickListener { dismiss() }
        btnCancel.setOnClickListener{ dismiss() }
    }

    override fun onStart()  {
        super.onStart()

    }
}