package com.example.lsb3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class AddEditCardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_card_activity)

        val chk = findViewById<CheckBox>(R.id.checkBox)
        val ltDisc = findViewById<LinearLayout>(R.id.ltDisc)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val etName = findViewById<EditText>(R.id.etName)
        val etShtr = findViewById<EditText>(R.id.etShtr)
        val etDisc = findViewById<EditText>(R.id.etDisc)
        val header = findViewById<TextView>(R.id.header)


        val intent = intent

        if (intent.hasExtra("name")) {
            header.text = "Редактировать карту"
            btnAdd.text = "Сохранить"
            etName.setText(intent.getStringExtra("name"))
            etShtr.setText(intent.getStringExtra("shtr"))
            chk.isChecked = !intent.getBooleanExtra("isDisc", true)
            ltDisc.isVisible = !chk.isChecked
            if (!chk.isChecked)
                etDisc.setText(intent.getStringExtra("disc"))
        }

        chk.setOnCheckedChangeListener{ checkBox, isChecked ->
            ltDisc.isVisible = !isChecked
        }

        btnAdd.setOnClickListener{
            var disc: String? = null
            if (etDisc.text.isNotEmpty())
                disc = etDisc.text.toString()

            var isDiscCorrect: Boolean = (chk.isChecked) || (!chk.isChecked && disc!=null && etDisc.text.toString().toInt()<100 )

            if (etName.text.isNotEmpty() && etShtr.text.isNotEmpty() && isDiscCorrect) {
                val returnIntent = Intent()
                returnIntent.putExtra("name", etName.text.toString())
                returnIntent.putExtra("shtr", etShtr.text.toString())
                returnIntent.putExtra("isDisc", !chk.isChecked)
                returnIntent.putExtra("disc", disc)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            else if(disc!=null && etDisc.text.toString().toInt()>=100){
                Toast.makeText(this, "Размер скидки не может быть больше 100%!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
/*маленький клас
        поле мятик
        поле шаик
        поле игушка

        метод играть(обект)
        метод обнимать(малышка)
        метод капризнитять(громкость)
 */