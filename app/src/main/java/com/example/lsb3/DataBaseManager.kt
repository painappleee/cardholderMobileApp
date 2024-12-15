package com.example.lsb3

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getLongOrNull

class DataBaseManager(context: Context): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CARDS.db"
        private const val DATABASE_VERSION = 3
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
                "FOREIGN KEY (id_store) REFERENCES Stores(id)"+
                ")")


        db.execSQL(queryCreateCards)
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



}
