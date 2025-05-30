package com.example.lsb3.ui.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.lsb3.MyApplication
import com.example.lsb3.R
import com.example.lsb3.data.model.Card
import com.example.lsb3.data.storages.AppSpecificStorageManager
import com.example.lsb3.data.storages.SharedStorageManager
import com.example.lsb3.databinding.ActivityMainBinding
import com.example.lsb3.ui.adapter.CardAdapter
import com.example.lsb3.ui.viewmodel.AddEditCardViewModel
import com.example.lsb3.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val appSpecificStorageManager: AppSpecificStorageManager = AppSpecificStorageManager(this)

    private var position = -1
    private var cards = ArrayList<Card>()
    private lateinit var adapter: CardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        adapter = CardAdapter(this, cards, { position ->
            val intent = Intent(this, ShowCardActivity::class.java).apply {
                putExtra("card", cards[position].copy())
            }
            this.startActivity(intent)
        })
        //cards = appSpecificStorageManager.read()


        binding.recView.adapter = adapter
        binding.recView.layoutManager = GridLayoutManager(this,2)

        // Привет, это я - твой код, не забывай меня коммитить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Тебе же хуже будет!




        viewModel.cards.observe(this) {
            updatedCards ->
            CoroutineScope(Dispatchers.IO).launch{
                cards.clear()
                cards.addAll(updatedCards)

                runOnUiThread{
                    adapter.notifyDataSetChanged()

                }
            }

        }




        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recView)

        binding.buttonAdd.setOnClickListener{
            intent = Intent(this, AddEditCardActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 121){
            val intent = Intent(this, AddEditCardActivity::class.java)

            position = item.groupId

            intent.putExtra("cardId",cards[position].id)
            startActivity(intent)
        }
        else if (item.itemId == 122){
            viewModel.saveBarCode(this,cards[item.groupId].shtrBitmap!!)
        }

        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch{
            viewModel.loadCardsWithBitmaps()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                intent = Intent(this, AddEditCardActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_save ->{
                viewModel.saveToPdf(this,cards)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    inner class SwipeToDeleteCallback(private val adapter: CardAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition

            CoroutineScope(Dispatchers.IO).launch{
                viewModel.deleteCard(cards[position].id)
            }

        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return when (viewHolder.bindingAdapterPosition % 2) {
                0 -> ItemTouchHelper.LEFT
                else -> ItemTouchHelper.RIGHT
            }
        }

    }

}

