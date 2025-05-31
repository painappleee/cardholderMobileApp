package com.example.lsb3.ui.adapter

import android.view.*

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lsb3.R
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.CardItemBinding
import com.example.lsb3.ui.fragments.MainFragment
import kotlin.collections.ArrayList


class CardAdapter(var context: MainFragment, var cards: ArrayList<Card>, var action: (Int) -> Unit): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {


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
            action(position)
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

            if(card.shopImg!=null){
                Glide.with(binding.root.context)
                    .load(card.shopImg)
                    .into(binding.iVShop)
            }



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