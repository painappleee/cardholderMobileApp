package com.example.lsb3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chk = findViewById<CheckBox>(R.id.checkBox)
        val ltDisc = findViewById<LinearLayout>(R.id.ltDisc)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val listView = findViewById<ListView>(R.id.listView)
        val etName = findViewById<EditText>(R.id.etName)
        val etShtr = findViewById<EditText>(R.id.etShtr)
        val etDisc = findViewById<EditText>(R.id.etDisc)

        val cards = ArrayList<Card>()

        chk.setOnCheckedChangeListener{ checkBox, isChecked ->
            ltDisc.isVisible = !isChecked
        }

        btnAdd.setOnClickListener{
            var disc: String? = null
            if (etDisc.text.toString()!="")
                disc = etDisc.text.toString()
            cards.add(Card(etName.text.toString(),etShtr.text.toString(),!chk.isChecked, disc))

            val adapter = CardAdapter(this, cards)
            listView.adapter = adapter


        }


    }





}