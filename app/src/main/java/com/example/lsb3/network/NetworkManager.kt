package com.example.lsb3.network

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class NetworkManager() {


    suspend fun imageExists(urlStr: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.connect()
                val responseCode = connection.responseCode
                connection.disconnect()
                responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun uploadImageToServer(context: Context, uri: Uri, fileNameWithoutExt: String): Boolean {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.readBytes() ?: return@withContext false
            val url = URL("http://10.0.2.2:5282/upload")


            val boundary = "Boundary-${System.currentTimeMillis()}"
            val connection = url.openConnection() as HttpURLConnection
            try {

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
            finally {
                if (coroutineContext.isActive.not()) {
                    connection?.disconnect()
                }
            }
        }
    }

}