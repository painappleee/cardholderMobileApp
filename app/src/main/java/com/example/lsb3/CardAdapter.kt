package com.example.lsb3

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream
import java.io.Serializable
import kotlin.collections.ArrayList


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
        holder.tvisDisc.text = if (cards[position].isDisc) "Дисконтая" else "Накопительная"
        holder.ltDisc.isVisible = cards[position].isDisc
        if (cards[position].isDisc)
            holder.tvDisc.text = cards[position].disc

        if(cards[position].shtrBitmap != null)
            holder.ivShtr.setImageBitmap(cards[position].shtrBitmap)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowCardActivity::class.java).apply {
                putExtra("card", cards[position].copy())
            }
            context.startActivity(intent)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(context)
        val convertView  = inflater.inflate(R.layout.card_item,parent, false)

        return CardViewHolder(convertView)
    }

    class CardViewHolder(convertView: View): RecyclerView.ViewHolder(convertView), View.OnCreateContextMenuListener {
        val tvName: TextView = convertView.findViewById(R.id.tVNameAns)
        val ivShtr: ImageView = convertView.findViewById(R.id.iVShtr)
        val tvisDisc: TextView = convertView.findViewById(R.id.tvisDiscAns)
        val ltDisc: LinearLayout = convertView.findViewById(R.id.ltDisc2)
        val tvDisc: TextView = convertView.findViewById(R.id.tvDiscAns)
        val itemlt: LinearLayout = convertView.findViewById(R.id.itemlt)

        init{
            itemlt.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(this.bindingAdapterPosition,121,0,"Редактировать")
            menu?.add(this.bindingAdapterPosition,122,1,"Сохранить штрих-код")

        }

    }



}