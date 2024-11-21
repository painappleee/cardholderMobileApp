package com.example.lsb3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible


class CardAdapter(var context: Context, var cards: ArrayList<Card>): BaseAdapter() {

    override fun getCount(): Int {
        return cards.size
    }

    override fun getItem(position: Int): Any {
        return cards[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val convertView  = inflater.inflate(R.layout.card_item_activity,null)
        val tvName = convertView.findViewById<TextView>(R.id.tVNameAns)
        tvName.text = cards[position].name
        val tvShtr = convertView.findViewById<TextView>(R.id.tvShtrAns)
        tvShtr.text = cards[position].shtr
        val tvisDisc = convertView.findViewById<TextView>(R.id.tvisDiscAns)
        val ltDisc = convertView.findViewById<LinearLayout>(R.id.ltDisc2)
        val tvDisc = convertView.findViewById<TextView>(R.id.tvDiscAns)
        ltDisc.isVisible = (cards[position].isDisc == true)
        if (cards[position].isDisc == true){
            tvisDisc.text = "Дисконтная"
            tvDisc.text = cards[position].disc
        }
        else{
            tvisDisc.text = "Накопительная"

        }
        return convertView
    }

}