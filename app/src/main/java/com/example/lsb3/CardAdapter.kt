package com.example.lsb3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(var context: Context, var cards: ArrayList<Card>): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.tvName.text = cards[position].name
        holder.tvShtr.text = cards[position].shtr
        holder.ivShtr.setImageBitmap(cards[position].shtrImg)
        holder.ltDisc.isVisible = (cards[position].isDisc == true)
        if (cards[position].isDisc == true){
            holder.tvisDisc.text = "Дисконтная"
            holder.tvDisc.text = cards[position].disc
        }
        else{
            holder.tvisDisc.text = "Накопительная"

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(context)
        val convertView  = inflater.inflate(R.layout.card_item,parent, false)

        return CardViewHolder(convertView)
    }

    class CardViewHolder(convertView: View): RecyclerView.ViewHolder(convertView){
        val tvName = convertView.findViewById<TextView>(R.id.tVNameAns)
        val tvShtr = convertView.findViewById<TextView>(R.id.tvShtrAns)
        val ivShtr = convertView.findViewById<ImageView>(R.id.iVShtr)
        val tvisDisc = convertView.findViewById<TextView>(R.id.tvisDiscAns)
        val ltDisc = convertView.findViewById<LinearLayout>(R.id.ltDisc2)
        val tvDisc = convertView.findViewById<TextView>(R.id.tvDiscAns)
    }
}