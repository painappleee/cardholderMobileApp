package com.example.lsb3.ui.view

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.lsb3.MyApplication
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.AddEditCardActivityBinding
import com.example.lsb3.ui.viewmodel.AddEditCardViewModel
import com.example.lsb3.ui.viewmodel.MainViewModel
import kotlinx.coroutines.*

class AddEditCardActivity: AppCompatActivity() {

    private lateinit var binding: AddEditCardActivityBinding
    private lateinit var viewModel: AddEditCardViewModel
    private var cardId: Int = -1
    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEditCardActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddEditCardViewModel::class.java)

        if (intent.hasExtra("cardId")) {
            isEdit = true
            binding.edit = true

            cardId = intent.getIntExtra("cardId",-1)

            CoroutineScope(Dispatchers.IO).launch{
                val card = viewModel.getCard(cardId)
                runOnUiThread {
                    binding.card = card
                }
            }

        }
        else{
            binding.edit = false
        }

        binding.checkBox.setOnCheckedChangeListener{ checkBox, isChecked ->
            binding.ltDisc.isVisible = !isChecked
        }

        binding.btnBack.setOnClickListener{
            val name = binding.etName.text.toString()
            val shtr = binding.etShtr.text.toString()
            val disc = binding.etDisc.text.toString()
            val isDisc = !binding.checkBox.isChecked

            if (name.isEmpty() || shtr.isEmpty() || (isDisc && (disc.isEmpty() || disc.toIntOrNull() == null || disc.toInt() >= 100))) {
                Toast.makeText(this, "Заполните все поля корректно", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedCard = Card(name, shtr, isDisc, if (isDisc) disc else null)

            CoroutineScope(Dispatchers.IO).launch{
                if (isEdit){
                    updatedCard.id = cardId
                    viewModel.editCard(updatedCard)
                }
                else{
                    viewModel.addCard(updatedCard)
                }

                withContext(Dispatchers.Main){
                    finish()
                }
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