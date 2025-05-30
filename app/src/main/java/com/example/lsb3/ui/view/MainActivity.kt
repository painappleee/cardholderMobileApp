package com.example.lsb3.ui.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val appSpecificStorageManager: AppSpecificStorageManager = AppSpecificStorageManager(this)

    private val sharedStorageManager: SharedStorageManager = SharedStorageManager(this)

    private var position = -1
    private var cards = ArrayList<Card>()
    private lateinit var adapter: CardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CardAdapter(this, cards)
        //cards = appSpecificStorageManager.read()


        binding.recView.adapter = adapter
        binding.recView.layoutManager = GridLayoutManager(this,2)

        // Привет, это я - твой код, не забывай меня коммитить!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Тебе же хуже будет!

        CoroutineScope(Dispatchers.Default).launch {
            val initialCards = MyApplication.dbManager.getAllCards().await()  // Твой метод, который возвращает список всех карт
            initialCards.forEach { card ->
                if (card.shtrBitmap == null) {
                    card.shtrBitmap = createBarCode(card.shtr)
                }
            }
            withContext(Dispatchers.Main) {
                cards.clear()
                cards.addAll(initialCards)
                adapter.notifyDataSetChanged()
            }
        }

        MyApplication.dbManager.cards.observe(this) { updatedCards ->
            CoroutineScope(Dispatchers.Default).launch {
                updatedCards.forEach { card ->
                    if (card.shtrBitmap == null) {
                        card.shtrBitmap = createBarCode(card.shtr)
                    }
                }
                withContext(Dispatchers.Main) {
                    cards.clear()
                    cards.addAll(updatedCards)
                    adapter.notifyDataSetChanged()

                }

            }
        }



        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recView)

        binding.buttonAdd.setOnClickListener{
            intent = Intent(this, AddEditCardActivity::class.java)
            //resultLauncher.launch(intent)
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
            cards[item.groupId].shtrBitmap?.let { sharedStorageManager.saveBarcode(it) }
        }

        return super.onContextItemSelected(item)
    }

    private fun createBarCode(codeData: String?): Bitmap? {
        return try {
            val hintMap = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

            val codeWriter = Code128Writer()
            val byteMatrix: BitMatrix = codeWriter.encode(
                codeData,
                BarcodeFormat.CODE_128,
                500,
                150,
                hintMap
            )
            val width = byteMatrix.width
            val height = byteMatrix.height
            val imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (i in 0 until width) {
                for (j in 0 until height) {
                    imageBitmap.setPixel(i, j, if (byteMatrix[i, j]) Color.BLACK else Color.WHITE)
                }
            }
            imageBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
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
                sharedStorageManager.saveToPdf(cards)
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
            MyApplication.dbManager.deleteCard(cards[position].id)

        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return when (viewHolder.bindingAdapterPosition % 2) {
                0 -> ItemTouchHelper.LEFT
                else -> ItemTouchHelper.RIGHT
            }
        }

    }

}

