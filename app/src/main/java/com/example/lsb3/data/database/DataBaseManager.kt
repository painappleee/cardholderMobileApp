package com.example.lsb3.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getLongOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lsb3.data.model.Card
import kotlinx.coroutines.*

class DataBaseManager(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val _cards = MutableLiveData<ArrayList<Card>>().apply {
        value = ArrayList()
    }
    val cards: LiveData<ArrayList<Card>> = _cards

    companion object {
        private const val DATABASE_NAME = "CARDS"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val queryCreateStore = ("CREATE TABLE Stores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL" +
                ")")

        db.execSQL(queryCreateStore)



        val queryCreateCards = ("CREATE TABLE Cards (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_store INTEGER NOT NULL,"+
                "barcode TEXT NOT NULL," +
                "isDisc BOOLEAN NOT NULL," +
                "disc INTEGER," +
                "FOREIGN KEY (id_store) REFERENCES Stores(id) ON DELETE CASCADE"+
                ")")


        db.execSQL(queryCreateCards)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Stores")
        db.execSQL("DROP TABLE IF EXISTS Cards")
        onCreate(db)
    }

    private suspend fun getStoreId(name: String): Int {
        var id = -1
        val db = this@DataBaseManager.writableDatabase
        val query = "SELECT id FROM Stores WHERE name = ?"

        val cursor: Cursor = db.rawQuery(query, arrayOf(name))
        id = if (cursor.moveToFirst()) {
            cursor.getLongOrNull(cursor.getColumnIndex("id"))?.toInt()!!
        } else {
            val values = ContentValues()
            values.put("name", name)
            db.insert("Stores", null, values).toInt()
        }


        cursor.close()
        return id
    }

    suspend fun getCardById(id: Int): Card? {
        val db = this@DataBaseManager.readableDatabase

        val query = ("SELECT " +
                "Cards.id AS id, " +
                "Cards.barcode, " +
                "Cards.isDisc, " +
                "Cards.disc, " +
                "Stores.name AS name " +
                "FROM Cards " +
                "INNER JOIN Stores ON Cards.id_store = Stores.id " +
                "WHERE Cards.id = ?")

        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var card: Card? = null

        if (cursor.moveToFirst()) {
            card = Card(
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("barcode")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isDisc")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("disc")).toString()
            )
            card.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }

        cursor.close()
        return card
    }


    suspend fun addCard(card: Card) {
        val db = this@DataBaseManager.writableDatabase

        var storeId: Int = -1
        var values: ContentValues? = null

        coroutineScope {
            var storeIdjob = launch {
                storeId = getStoreId(card.name)
            }

            storeIdjob.join()


            launch {
                values = ContentValues().apply {
                    put("id_store", storeId)
                    put("barcode", card.shtr)
                    put("isDisc", card.isDisc)
                    if (card.isDisc)
                        put("disc", card.disc!!.toInt())
                }
            }
        }

        db.insert("Cards", null, values)

        refreshCards()

    }

    private suspend fun refreshCards() {
        val cardList = getAllCards()
        _cards.postValue(cardList)
    }

    suspend fun getAllCards(): ArrayList<Card> {
        val cardsList = ArrayList<Card>()
        val db = this@DataBaseManager.readableDatabase

        val queryAllCards = ("SELECT " +
                "Cards.id AS id, " +
                "Cards.barcode, " +
                "Cards.isDisc, " +
                "Cards.disc, " +
                "Stores.name AS name " +
                "FROM Cards " +
                "INNER JOIN Stores " +
                "ON Cards.id_store = Stores.id")

        val cursor = db.rawQuery(queryAllCards, null)

        while (cursor.moveToNext()) {
            val card = Card(
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("barcode")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isDisc")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("disc")).toString()
            )
            card.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            cardsList.add(card)
        }

        cursor.close()

        return cardsList
    }

    suspend fun deleteCard(id: Int){
        val db = this@DataBaseManager.writableDatabase

        val queryDeleteCard = "DELETE FROM Cards WHERE id = ?"
        db.execSQL(queryDeleteCard, arrayOf(id))

        refreshCards()

    }


    suspend fun editCard(card: Card){
        val db = this@DataBaseManager.writableDatabase

        val queryUpdateCard = ("UPDATE Cards " +
                "SET id_store = ?, barcode = ?, isDisc = ?, disc = ? " +
                "WHERE id = ?")

        val disc = if (card.isDisc)
            card.disc!!.toInt()
        else
            null

        db.execSQL(queryUpdateCard, arrayOf(getStoreId(card.name), card.shtr, card.isDisc, disc, card.id))

        refreshCards()
    }

    private suspend fun deleteStore(id: Int){
        val db = this@DataBaseManager.writableDatabase

        val queryDeleteCard = "DELETE FROM Stores WHERE id = ?"
        db.execSQL(queryDeleteCard, arrayOf(id))
    }

}
