package com.example.lsb3

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lsb3.databinding.ShowCardBinding

class ShowCardActivity : AppCompatActivity() {


    private lateinit var binding: ShowCardBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ShowCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val card = intent.getSerializableExtra("card", Card::class.java)!!

        binding.card = card
        binding.shtrImage.setImageBitmap(BitmapConverter.scaledBitmap(card.shtrBitmap!!,3f))

        binding.btnBack.setOnClickListener {
            finish()
        }


    }
}