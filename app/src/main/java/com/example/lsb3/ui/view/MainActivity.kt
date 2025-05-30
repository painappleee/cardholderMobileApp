package com.example.lsb3.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lsb3.R
import com.example.lsb3.databinding.ActivityMainBinding
import com.example.lsb3.ui.fragments.MainFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frlayout, MainFragment())
            .commit()
    }
}

