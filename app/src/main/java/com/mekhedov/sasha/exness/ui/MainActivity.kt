package com.mekhedov.sasha.exness.ui

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.mekhedov.sasha.exness.R
import com.mekhedov.sasha.exness.mvp.MainView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mekhedov.sasha.exness.BaseApplication
import com.mekhedov.sasha.exness.common.RxBus
import com.mekhedov.sasha.exness.mvp.MainPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import rx.Scheduler
import javax.inject.Inject
import android.R.menu
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuInflater
import android.view.MenuItem
import com.mekhedov.sasha.exness.common.Data
import com.mekhedov.sasha.exness.common.SubscribeEvent
import com.mekhedov.sasha.exness.common.UnsubscribeEvent
import com.mekhedov.sasha.exness.mvp.MainModel
import com.vicpin.krealmextensions.*
import com.vicpin.krealmextensions.rx.queryAllAsObservable
import io.realm.Realm
import io.realm.Sort


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var presenter: MainPresenter

    @ProvidePresenter(type = PresenterType.GLOBAL)
    fun provideresenter() = MainPresenter()

    @Inject
    lateinit var rxBus : RxBus

    var list : ArrayList<MainModel> = ArrayList<MainModel>()
    lateinit var adapter : MainAdapter
    lateinit var dialog : SettingsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collapsingToolbarLayout.setTitle("Exness");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        BaseApplication().appComponent.inject(presenter)
        BaseApplication().appComponent.inject(this)


        //list = listOf(MainModel("proba", "proba", "proba", "proba"))
        //init database
        //MainModel().deleteAll()
        if (MainModel().queryAll().size == 0)
        {
            val currencyList = listOf(
                    "EURUSD",
                    "EURGBP",
                    "USDJPY",
                    "GBPUSD",
                    "USDCHF",
                    "USDCAD",
                    "AUDUSD",
                    "EURJPY",
                    "EURCHF")
            var i:Int = 0
            for (currency:String in currencyList) {
                MainModel(currency, true, i).save()
                i++
            }
        }

        adapter = MainAdapter(this, list)
        rvMain.adapter = adapter;
        rvMain.layoutManager = LinearLayoutManager(this)

        dialog = SettingsDialog(this)

        rxBus.listen(String::class.java).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    updateView()
                    //Toast.makeText(this, "message", Toast.LENGTH_SHORT).show()
                })
        rxBus.listen(SubscribeEvent::class.java).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    Log.d(Data().TAG, it.name)
                    var m = MainModel().queryFirst()  { query -> query.equalTo("name",it.name) }
                    if (m!=null) {
                        m.isActive = true
                        m.save()
                    }
                    presenter.send("SUBSCRIBE: "+it.name)
                })
        rxBus.listen(UnsubscribeEvent::class.java).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    Log.d(Data().TAG, it.name)
                    var m = MainModel().queryFirst()  { query -> query.equalTo("name",it.name) }
                    if (m!=null) {
                        m.isActive = false
                        m.save()
                    }
                    presenter.send("UNSUBSCRIBE: "+it.name)
                })
        val modelObserver = MainModel().queryAllAsObservable()
        modelObserver.asObservable().subscribe {
            updateView()
            //Toast.makeText(this, "realm", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId())
        {
            R.id.menuHelp -> {
                Log.d(Data().TAG, "Help")
                var alert = AlertDialog.Builder(this);
                alert.setTitle("Exness")
                alert.setMessage(R.string.info)
                alert.show()
                return true
            }
            R.id.menuRefresh -> {
                Log.d(Data().TAG, "Refresh")
                presenter.connect()
                return true
            }
            R.id.menuList ->    {
                Log.d(Data().TAG, "List")
                dialog.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onStart() {
        super.onStart()
        presenter.connect()
    }

    override fun onStop()   {
        super.onStop()
        presenter.disconnect()
    }

    override fun onConnect()    {
        //Toast.makeText(this, "connect", Toast.LENGTH_SHORT).show()
        Log.d("Okhttp3", "connect")
        var currencies = MainModel().querySorted("sort", Sort.ASCENDING) { query -> query.equalTo("isActive",true) }
        var str = ""
        for (item in currencies)    {
            if (str.isEmpty())
                str += item.name
            else
                str += ","+item.name
        }
        if (!str.isEmpty()) {
            presenter.send("SUBSCRIBE: "+str)
        }
    }

    fun updateView()    {
        list.clear()
        list.addAll(MainModel().querySorted("sort", Sort.ASCENDING) { query -> query.equalTo("isActive",true) } )
/*        for (item in list)  {
            print(item)
            Log.d(Data().TAG, item.name)
        }*/
        adapter.notifyDataSetChanged()
/*        Realm.getDefaultInstance().use {
            val list = it.where(MainModel::class.java).findAll()
            print(list)
        }*/
    }
}
