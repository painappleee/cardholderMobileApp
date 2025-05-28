package com.example.lsb3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lsb3.databinding.AddEditCardActivityBinding

class AddEditCardActivity: AppCompatActivity() {

    private lateinit var binding: AddEditCardActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEditCardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent

        if (intent.hasExtra("name")) {
            binding.header.text = "Редактировать карту"
            binding.btnBack.text = "Сохранить"
            binding.etName.setText(intent.getStringExtra("name"))
            binding.etShtr.setText(intent.getStringExtra("shtr"))
            binding.checkBox.isChecked = !intent.getBooleanExtra("isDisc", true)
            binding.ltDisc.isVisible = !binding.checkBox.isChecked
            if (!binding.checkBox.isChecked)
                binding.etDisc.setText(intent.getStringExtra("disc"))
        }

        binding.checkBox.setOnCheckedChangeListener{ checkBox, isChecked ->
            binding.ltDisc.isVisible = !isChecked
        }

        binding.btnBack.setOnClickListener{
            var disc: String? = null
            if (binding.etDisc.text.isNotEmpty())
                disc = binding.etDisc.text.toString()

            var isDiscCorrect: Boolean = (binding.checkBox.isChecked) || (!binding.checkBox.isChecked && disc!=null && binding.etDisc.text.toString().toInt()<100 )

            if (binding.etName.text.isNotEmpty() && binding.etShtr.text.isNotEmpty() && isDiscCorrect) {
                val returnIntent = Intent()
                returnIntent.putExtra("name", binding.etName.text.toString())
                returnIntent.putExtra("shtr", binding.etShtr.text.toString())
                returnIntent.putExtra("isDisc", !binding.checkBox.isChecked)
                returnIntent.putExtra("disc", disc)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            else if(disc!=null && binding.etDisc.text.toString().toInt()>=100){
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