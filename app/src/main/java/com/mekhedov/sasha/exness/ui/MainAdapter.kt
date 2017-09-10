package com.mekhedov.sasha.exness.ui

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mekhedov.sasha.exness.R
import com.mekhedov.sasha.exness.mvp.MainModel
import kotlinx.android.synthetic.main.item_main.*

/**
 * Created by Elena on 10.09.2017.
 */

class MainAdapter(private val context : Context, private val list : List<MainModel>) : RecyclerView.Adapter<MainAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvValue: TextView
        var tvSpread : TextView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
            tvValue = itemView.findViewById(R.id.tvValue) as TextView
            tvSpread = itemView.findViewById(R.id.tvSpread) as TextView
        }
    }
    override fun onCreateViewHolder(parent : ViewGroup, type : Int) : MainAdapter.ViewHolder{
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false);
        val card = view.findViewById(R.id.card_view) as CardView
        //   card.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        card.maxCardElevation = 2.0F;
        card.radius = 5.0F;
        return ViewHolder(view);
    }
    override fun onBindViewHolder(holder : MainAdapter.ViewHolder, position : Int){
        var mainModel : MainModel = list.get(position)
        holder.tvTitle.text = mainModel.name;
        holder.tvValue.text = "ask/bid: ${mainModel.ask}/${mainModel.bid}"
        holder.tvSpread.text = "spread: ${mainModel.spread}"
    }
    override fun getItemCount() : Int{
        return list.size;
    }

}