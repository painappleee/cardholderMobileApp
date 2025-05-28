package com.example.lsb3

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


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

}