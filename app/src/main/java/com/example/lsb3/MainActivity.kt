package com.example.lsb3

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var resultEditLauncher: ActivityResultLauncher<Intent>

    private var position = -1
    private val cards = ArrayList<Card>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recView = findViewById<RecyclerView>(R.id.recView)
        val adapter = CardAdapter(this, cards)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)


        recView.adapter = adapter
        recView.layoutManager = GridLayoutManager(this,2)


        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(recView)


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val card = Card(
                    data?.getStringExtra("name"),
                    data?.getStringExtra("shtr"),
                    data?.getBooleanExtra("isDisc",false),
                    data?.getStringExtra("disc")
                )
                card.shtrImg = createBarCode(card.shtr)
                cards.add(card)
                adapter.notifyItemInserted(cards.size - 1)

            }
        }

        resultEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val card = Card(
                    data?.getStringExtra("name"),
                    data?.getStringExtra("shtr"),
                    data?.getBooleanExtra("isDisc",false),
                    data?.getStringExtra("disc")
                )
                card.shtrImg = createBarCode(card.shtr)
                cards[position] = card
                adapter.notifyItemChanged(position)
                position = -1

            }
        }

        buttonAdd.setOnClickListener{
            intent = Intent(this, AddEditCardActivity::class.java)
            resultLauncher.launch(intent)
        }



    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, AddEditCardActivity::class.java)

        position = item.groupId
        intent.putExtra("name", cards[position].name)
        intent.putExtra("shtr", cards[position].shtr)
        intent.putExtra("isDisc", cards[position].isDisc)
        if ( cards[item.groupId].isDisc!=false)
            intent.putExtra("disc", cards[position].disc)

        resultEditLauncher.launch(intent)

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
                resultLauncher.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    class SwipeToDeleteCallback(private val adapter: CardAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            adapter.removeItem(position)

        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return when (viewHolder.bindingAdapterPosition % 2) {
                0 -> ItemTouchHelper.LEFT
                else -> ItemTouchHelper.RIGHT
            }
        }

    }

}

