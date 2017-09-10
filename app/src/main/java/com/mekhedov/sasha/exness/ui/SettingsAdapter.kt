package com.mekhedov.sasha.exness.ui

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import com.mekhedov.sasha.exness.BaseApplication
import com.mekhedov.sasha.exness.R
import com.mekhedov.sasha.exness.common.Data
import com.mekhedov.sasha.exness.common.RxBus
import com.mekhedov.sasha.exness.common.SubscribeEvent
import com.mekhedov.sasha.exness.common.UnsubscribeEvent
import com.mekhedov.sasha.exness.mvp.MainModel
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.querySorted
import com.vicpin.krealmextensions.save
import io.realm.Sort
import javax.inject.Inject

/**
 * Created by Elena on 10.09.2017.
 */

class SettingsAdapter(private val context : Context, private val list : ArrayList<MainModel>) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>(){

    @Inject
    lateinit var rxBus : RxBus

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var chBox: CheckBox
        var ivUp: ImageView
        var ivDown: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
            chBox = itemView.findViewById(R.id.chBox) as CheckBox
            ivUp = itemView.findViewById(R.id.ivUp) as ImageView
            ivDown = itemView.findViewById(R.id.ivDown) as ImageView
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, type : Int) : SettingsAdapter.ViewHolder{
        BaseApplication().appComponent.inject(this)

        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false);
        val card = view.findViewById(R.id.card_view) as CardView
        //   card.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        card.maxCardElevation = 2.0F;
        card.radius = 5.0F;
        return ViewHolder(view);
    }
    override fun onBindViewHolder(holder : SettingsAdapter.ViewHolder, position : Int){
        var mainModel : MainModel = list.get(position)
        holder.tvTitle.text = mainModel.name;
        holder.chBox.setChecked(mainModel.isActive)
        holder.chBox.setTag(mainModel.name)
        holder.chBox.setOnClickListener() {
                Log.d(Data().TAG, "checkbox("+it.getTag().toString()+")")
                var v = it as CheckBox
                if (v.isChecked())
                    rxBus.publish(SubscribeEvent(it.getTag().toString()))
                else
                    rxBus.publish(UnsubscribeEvent(it.getTag().toString()))
        }

        holder.ivUp.setTag(mainModel.name)
        holder.ivUp.setOnClickListener() {
            var m = MainModel().queryFirst(){ query -> query.equalTo("name",it.getTag().toString()) }
            if (m!=null)    {
                var sort = m.sort
                if (sort > 0)
                {
                    var pos = sort
                    pos--
                    if (pos >= 0) {
                        var mm = MainModel().queryFirst() { query -> query.equalTo("sort", pos) }
                        if (mm != null) {
                            mm.sort = sort
                            mm!!.save()

                            m.sort = pos
                            m.save()
                            this.list.clear()
                            this.list.addAll(MainModel().querySorted("sort", Sort.ASCENDING))
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        holder.ivDown.setTag(mainModel.name)
        holder.ivDown.setOnClickListener() {
            var m = MainModel().queryFirst(){ query -> query.equalTo("name",it.getTag().toString()) }
            if (m!=null)    {
                var sort = m.sort
                if (sort <=  getItemCount())
                {
                    var pos = sort
                    pos++
                    if (pos <= getItemCount()) {
                        var mm = MainModel().queryFirst() { query -> query.equalTo("sort", pos) }
                        if (mm != null) {
                            mm.sort = sort
                            mm!!.save()

                            m.sort = pos
                            m.save()
                            this.list.clear()
                            this.list.addAll(MainModel().querySorted("sort", Sort.ASCENDING))
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    override fun getItemCount() : Int{
        return list.size;
    }

}