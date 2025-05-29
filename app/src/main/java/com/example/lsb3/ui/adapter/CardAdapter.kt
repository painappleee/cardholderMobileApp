package com.example.lsb3.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.*

import androidx.recyclerview.widget.RecyclerView
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.CardItemBinding
import com.example.lsb3.ui.view.ShowCardActivity
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

        holder.bind(cards[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowCardActivity::class.java).apply {
                putExtra("card", cards[position].copy())
            }
            context.startActivity(intent)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CardViewHolder(binding)
    }

    class CardViewHolder(private val binding: CardItemBinding): RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        fun bind(card: Card){
            binding.card = card
            binding.iVShtr.setImageBitmap(card.shtrBitmap)
            binding.executePendingBindings()
        }


        init{
            binding.itemlt.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(this.bindingAdapterPosition,121,0,"Редактировать")
            menu?.add(this.bindingAdapterPosition,122,1,"Сохранить штрих-код")

        }

    }



}