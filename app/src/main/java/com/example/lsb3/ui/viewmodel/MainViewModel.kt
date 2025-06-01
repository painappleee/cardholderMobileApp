package com.example.lsb3.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.lsb3.MyApplication
import com.example.lsb3.data.database.DataBaseManager
import com.example.lsb3.data.model.Card
import com.example.lsb3.data.storages.SharedStorageManager
import com.example.lsb3.network.NetworkManager
import com.example.lsb3.ui.view.MainActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(private val dbManager: DataBaseManager, private val networkManager: NetworkManager): ViewModel() {


    private val _cards = MutableLiveData<ArrayList<Card>>()
    val cards: LiveData<ArrayList<Card>> get() = _cards

    init {
        viewModelScope.launch{
            loadCardsWithBitmaps()
        }
    }


    private suspend fun loadCardsWithBitmaps() {
        val rawCards = withContext(Dispatchers.IO) {
            val cards = dbManager.getAllCards()
            for (card in cards){
                card.shtrBitmap = createBarCode(card.shtr)
                val url = "http://10.0.2.2:5282/images/${card.name}.png"
                card.shopImg = if (networkManager.imageExists(url)) url else null
            }
            cards
        }
        _cards.postValue(rawCards)

    }


    private fun createBarCode(codeData: String?): Bitmap? {
        return try {
            val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

            val codeWriter = Code128Writer()
            val byteMatrix: BitMatrix = codeWriter.encode(
                codeData,
                BarcodeFormat.CODE_128,
                500,
                150,
                hintMap
            )
            val width = byteMatrix.width
            val height = byteMatrix.height
            val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (i in 0 until width) {
                for (j in 0 until height) {
                    imageBitmap.setPixel(i, j, if (byteMatrix[i, j]) Color.BLACK else Color.WHITE)
                }
            }
            imageBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }

    }

    suspend fun deleteCard(cardId: Int){
        dbManager.deleteCard(cardId)
    }

    fun saveBarCode(context: Context, bitmap: Bitmap){
        if (bitmap!=null)
            SharedStorageManager.saveBarcode(context,bitmap)
    }

    fun saveToPdf(context: Context, cards: ArrayList<Card>){
        SharedStorageManager.saveToPdf(context,cards)
    }
}