package com.example.lsb3.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lsb3.R
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.FragmentShowCardBinding


import com.example.lsb3.ui.viewmodel.MainViewModel
import com.example.lsb3.utils.BitmapConverter

class ShowCardFragment : Fragment() {

    private lateinit var binding: FragmentShowCardBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentShowCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val card = arguments?.getSerializable("card", Card::class.java)

        binding.card = card
        binding.shtrImage.setImageBitmap(BitmapConverter.scaledBitmap(card?.shtrBitmap!!, 3f))

        binding.btnBack.setOnClickListener {
            finish()
        }



    }

    private fun finish(){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frlayout, MainFragment())
            .commit()
    }
}