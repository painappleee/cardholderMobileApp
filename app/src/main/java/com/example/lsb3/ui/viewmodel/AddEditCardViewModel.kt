package com.example.lsb3.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lsb3.MyApplication
import com.example.lsb3.data.model.Card
import kotlinx.coroutines.*

class AddEditCardViewModel:ViewModel() {
    suspend fun getCard(cardId: Int): Card? {
        return MyApplication.dbManager.getCardById(cardId)
    }

    suspend fun addCard(card: Card){
        MyApplication.dbManager.addCard(card)
    }

    suspend fun editCard(card: Card){
        MyApplication.dbManager.editCard(card)
    }
}
