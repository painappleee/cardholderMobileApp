package com.example.lsb3

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getLongOrNull

class DataBaseManager(context: Context): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {

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

    private fun getStoreId(name: String): Int {
        var id = -1
        val db = this.writableDatabase
        val query = "SELECT id FROM Stores WHERE name = ?"

        val cursor: Cursor = db.rawQuery(query, arrayOf(name))
        id = if (cursor.moveToFirst()) {
            cursor.getLongOrNull(cursor.getColumnIndex("id"))?.toInt()!!
        } else {
            val values = ContentValues()
            values.put("name", name)
            db.insert("Stores", null, values).toInt()
        }

        return id
    }

    fun getCardId(card: Card): Int {
        var id = -1
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put("id_store", getStoreId(card.name))
            put("barcode", card.shtr)
            put("isDisc", card.isDisc)
            if (card.isDisc)
                put("disc", card.disc!!.toInt())
        }
        id = db.insert("Cards", null, values).toInt()

        return id
    }

    fun getAllCards(): ArrayList<Card> {
        val cards = ArrayList<Card>()
        val db = this.writableDatabase

        deleteStore(4)

        val queryAllCards = ("SELECT "+
                    "Cards.id AS id, "+
                    "Cards.barcode, "+
                    "Cards.isDisc, "+
                    "Cards.disc, "+
                    "Stores.name AS name "+
                    "FROM Cards " +
                    "INNER JOIN Stores "+
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
            cards.add(card)
        }
        return cards
    }

    fun deleteCard(id: Int){
        val db = this.writableDatabase

        val queryDeleteCard = "DELETE FROM Cards WHERE id = ?"
        db.execSQL(queryDeleteCard, arrayOf(id))


    }

    fun editCard(id:Int, card: Card){
        val db = this.writableDatabase

        val queryUpdateCard = ("UPDATE Cards "+
        "SET id_store = ?, barcode = ?, isDisc = ?, disc = ? "+
        "WHERE id = ?")

        val disc = if (card.isDisc)
            card.disc!!.toInt()
        else
            null

        db.execSQL(queryUpdateCard, arrayOf(getStoreId(card.name), card.shtr, card.isDisc, disc, id))

    }

    private fun deleteStore(id: Int){
        val db = this.writableDatabase

        val queryDeleteCard = "DELETE FROM Stores WHERE id = ?"
        db.execSQL(queryDeleteCard, arrayOf(id))
    }




}

