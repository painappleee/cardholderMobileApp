package com.example.lsb3.data.model

import android.graphics.Bitmap
import com.example.lsb3.R
import com.example.lsb3.utils.BitmapConverter
import java.io.Serializable

data class Card(
    var name: String,
    var shtr: String,
    var isDisc: Boolean,
    var disc: String? = null,
    var shtrImg: ByteArray? = null,
    var id: Int =0,
    var shopImg: Int = R.drawable.nophoto
): Serializable {
    var shtrBitmap: Bitmap?
        get() = BitmapConverter.byteArrayToBitmap(this.shtrImg)
        set(value){
            this.shtrImg = BitmapConverter.bitmapToByteArray(value!!)
        }

}