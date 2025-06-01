package com.example.lsb3.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream
import java.util.*


object BitmapConverter {
    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap?{
        if(byteArray == null)
            return null

        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return bitmap
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
        val byteArray = stream.toByteArray()
        return byteArray;
    }

    fun scaledBitmap(bitmap: Bitmap, scale: Float): Bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), false)

    fun createBarCode(codeData: String?): Bitmap? {
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
}