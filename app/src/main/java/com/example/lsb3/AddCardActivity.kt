package com.example.lsb3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class AddCardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_card_activity)

        val chk = findViewById<CheckBox>(R.id.checkBox)
        val ltDisc = findViewById<LinearLayout>(R.id.ltDisc)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val etName = findViewById<EditText>(R.id.etName)
        val etShtr = findViewById<EditText>(R.id.etShtr)
        val etDisc = findViewById<EditText>(R.id.etDisc)


        chk.setOnCheckedChangeListener{ checkBox, isChecked ->
            ltDisc.isVisible = !isChecked
        }

        btnAdd.setOnClickListener{
            var disc: String? = null
            if (etDisc.text.isNotEmpty())
                disc = etDisc.text.toString()

            var isDiscCorrect: Boolean = (chk.isChecked) || (!chk.isChecked && etDisc.text.isNotEmpty())

            if (etName.text.isNotEmpty() && etShtr.text.isNotEmpty() && isDiscCorrect) {
                val returnIntent = Intent()
                returnIntent.putExtra("name", etName.text.toString())
                returnIntent.putExtra("shtr", etShtr.text.toString())
                returnIntent.putExtra("isDisc", !chk.isChecked)
                returnIntent.putExtra("disc", disc)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            else{
                Toast.makeText(applicationContext, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}