package com.example.lsb3.ui.view

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.ShowCardBinding
import com.example.lsb3.utils.BitmapConverter

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