package com.mekhedov.sasha.exness.mvp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.Bidi

/**
 * Created by Elena on 09.09.2017.
 */
open class MainModel() : RealmObject() {
    @PrimaryKey
    open var name: String = ""

    open var bid: String = ""

    open var ask: String = ""

    open var spread: String = ""

    constructor(name: String, ask: String, bid: String, spread: String) : this() {
        this.name = name
        this.ask = ask
        this.bid = bid
        this.spread = spread
    }
}