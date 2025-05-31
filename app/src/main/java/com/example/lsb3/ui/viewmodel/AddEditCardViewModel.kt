package com.example.lsb3.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.lsb3.MyApplication
import com.example.lsb3.data.model.Card
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

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

    suspend fun uploadImageToServer(context: Context, uri: Uri, fileNameWithoutExt: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes() ?: return@withContext false
                val url = URL("http://10.0.2.2:5282/upload")

                val boundary = "Boundary-${System.currentTimeMillis()}"
                val connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

                val output = DataOutputStream(connection.outputStream)
                output.writeBytes("--$boundary\r\n")
                output.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"$fileNameWithoutExt.png\"\r\n")
                output.writeBytes("Content-Type: image/png\r\n\r\n")
                output.write(fileBytes)
                output.writeBytes("\r\n--$boundary--\r\n")
                output.flush()
                output.close()

                connection.responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
