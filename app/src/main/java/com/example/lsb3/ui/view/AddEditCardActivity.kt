package com.example.lsb3.ui.view

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.lsb3.MyApplication
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.AddEditCardActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class AddEditCardActivity: AppCompatActivity() {

    private lateinit var binding: AddEditCardActivityBinding
    private lateinit var card: Card

    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEditCardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent

        if (intent.hasExtra("cardId")) {
            binding.edit = true
            isEdit = true


            CoroutineScope(Dispatchers.IO).async {
                card = MyApplication.dbManager.getCardById(intent.getIntExtra("cardId", -1)).await()!!

                withContext(Dispatchers.Main){
                    Log.d("isDisc",card.isDisc.toString())
                    binding.card = card

                }
            }


        }
        else{
            binding.edit = false
            isEdit = false
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

                val name = binding.etName.text.toString()
                val shtr = binding.etShtr.text.toString()
                val isDisc = !binding.checkBox.isChecked
                val disc = binding.etDisc.text.toString()

                val updatedCard = Card(name, shtr, isDisc, disc)


                if (isEdit){
                    updatedCard.id = card.id
                    MyApplication.dbManager.editCard(updatedCard)
                }
                else{
                    MyApplication.dbManager.addCard(updatedCard)
                }

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