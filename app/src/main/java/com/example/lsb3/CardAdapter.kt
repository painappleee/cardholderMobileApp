package com.example.lsb3

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.View.OnLongClickListener


class CardAdapter(var context: Context, var cards: ArrayList<Card>): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {


    fun removeItem(position: Int) {
        cards.removeAt(position)
        notifyItemRemoved(position)
    }

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
        else {
            holder.tvisDisc.text = "Накопительная"
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(context)
        val convertView  = inflater.inflate(R.layout.card_item,parent, false)

        return CardViewHolder(convertView)
    }

    class CardViewHolder(convertView: View): RecyclerView.ViewHolder(convertView), View.OnCreateContextMenuListener {
        val tvName: TextView = convertView.findViewById(R.id.tVNameAns)
        val tvShtr: TextView = convertView.findViewById(R.id.tvShtrAns)
        val ivShtr: ImageView = convertView.findViewById(R.id.iVShtr)
        val tvisDisc: TextView = convertView.findViewById(R.id.tvisDiscAns)
        val ltDisc: LinearLayout = convertView.findViewById(R.id.ltDisc2)
        val tvDisc: TextView = convertView.findViewById(R.id.tvDiscAns)
        private val item: LinearLayout = convertView.findViewById(R.id.item)

        init{
            item.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(this.bindingAdapterPosition,121,0,"Редактировать")
        }






    }


}