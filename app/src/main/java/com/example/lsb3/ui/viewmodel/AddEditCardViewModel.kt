package com.example.lsb3.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lsb3.MyApplication
import com.example.lsb3.data.database.DataBaseManager
import com.example.lsb3.data.model.Card
import com.example.lsb3.network.NetworkManager
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AddEditCardViewModel(private val dbManager: DataBaseManager, private val networkManager: NetworkManager):ViewModel() {
    suspend fun getCard(cardId: Int): Card? {
        var card = dbManager.getCardById(cardId)

        val url = "http://10.0.2.2:5282/images/${card?.name}.png"
        card?.shopImg = if (networkManager.imageExists(url)) url else null

        return card
    }

    private lateinit var job: Job

    suspend fun uploadImageToServer(context: Context, uri: Uri, fileNameWithoutExt: String) {

     job = viewModelScope.launch {
            try {
                delay(5000)

                networkManager.uploadImageToServer(context, uri, fileNameWithoutExt)
            } catch (e: CancellationException) {
                Log.d("canceled","true")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelCoroutine(){
        job.cancel()
    }

    suspend fun addCard(card: Card){
        dbManager.addCard(card)
    }

    suspend fun editCard(card: Card){
        dbManager.editCard(card)
    }


}
