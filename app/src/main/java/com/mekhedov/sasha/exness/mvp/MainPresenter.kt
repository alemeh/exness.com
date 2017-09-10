package com.mekhedov.sasha.exness.mvp

import android.util.Log
import android.widget.Toast
import com.appunite.websocket.rx.RxWebSockets
import com.appunite.websocket.rx.`object`.ObjectSerializer
import com.appunite.websocket.rx.`object`.RxObjectWebSockets
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mekhedov.sasha.exness.BaseApplication
import com.mekhedov.sasha.exness.common.Data
import com.mekhedov.sasha.exness.common.RxBus
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Elena on 09.09.2017.
 */

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var rxBus: RxBus

    lateinit var webSocket : WebSocket

    fun connect() {
        val request : Request = Request.Builder()
                .get()
                .url("wss://quotes.exness.com:18400")
                .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                viewState.onConnect()
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                Log.d(Data().TAG_JSON, text)
                try{
                    val jsonObject = JSONObject(text);
                    var count : String = jsonObject.optString("subscribed_count")
                    var listObj = jsonObject.optJSONObject("subscribed_list")
                    var tickArray = listObj.optJSONArray("ticks")
                    for (i in 0..(tickArray.length() - 1)) {
                        var tickItem : JSONObject = tickArray.getJSONObject(i)
                        var name = tickItem.optString("s")
                        Log.d(Data().TAG_JSON, "name: "+name)
                        var ask = tickItem.optString("a")
                        Log.d(Data().TAG_JSON, "ask: "+ask)
                        var bid = tickItem.optString("b")
                        Log.d(Data().TAG_JSON, "bid: "+bid)
                        var spread = tickItem.optString("spr")
                        Log.d(Data().TAG_JSON, "spread: "+spread)

                        //MainModel(name, ask, bid, spread).save()
                        var mainModel = MainModel().queryFirst{ query -> query.equalTo("name",name) }
                        if (mainModel == null)  {
                            mainModel = MainModel(name, ask, bid, spread)
                        }   else    {
                            mainModel.ask = ask
                            mainModel.bid = bid
                            mainModel.spread = spread
                        }
                        Observable.just(mainModel).
                                subscribeOn(AndroidSchedulers.mainThread()).
                                observeOn(Schedulers.io()).
                                subscribe({
                                    //save in background thread
                                    try {
                                        it.save()
                                    }   catch (e:Exception) {
                                        e.printStackTrace()
                                    }
/*                                    val realm = Realm.getDefaultInstance()
                                    try {
                                        realm.beginTransaction();
                                        realm.copyToRealmOrUpdate(it);
                                        realm.commitTransaction();
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        realm.close();
                                        rxBus.publish("ok")
                                    }*/
                                })
                    }
                } catch (e:Exception){}

//                var list = MainModel().queryAll()
//                print(list)

            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                Log.d("Okhttp3", "text")
            }
            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                Log.d("Okhttp3", "failure")
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosing(webSocket, code, reason)
                Log.d("Okhttp3", "closing")
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                Log.d("Okhttp3", "closed")
            }
        })
    }

    fun send(str : String)  {
        webSocket.send(str)
    }

    fun disconnect()    {
        try {
            webSocket.close(1000, "GoodBye!")
        } catch(e:Exception)    {
            e.printStackTrace()
        }
    }
}