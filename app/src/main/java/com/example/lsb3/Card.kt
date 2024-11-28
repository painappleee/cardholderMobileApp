package com.example.lsb3

import android.graphics.Bitmap

data class Card(var name: String?, var shtr: String? ,var isDisc: Boolean?, var disc: String? = null){
    var shtrImg: Bitmap? = null
}