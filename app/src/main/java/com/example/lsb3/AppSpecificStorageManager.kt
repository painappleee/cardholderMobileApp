package com.example.lsb3

import android.content.Context
import android.icu.text.Transliterator.Position
import java.io.IOException

class AppSpecificStorageManager(private val context: Context) {

    private val filename = "cards.txt"

    fun write(card: Card){
        try {
            context.openFileOutput(filename, Context.MODE_APPEND).use { outputStream ->
                outputStream.bufferedWriter().use { writer ->
                    writer.write(card.shtr)
                    writer.newLine()
                    writer.write(card.name)
                    writer.newLine()
                    if (card.isDisc == true){
                        writer.write("1")
                        writer.write(" ")
                        writer.write(card.disc)
                    }
                    else{
                        writer.write("0")
                    }
                    writer.newLine()
                }
            }


        }
        catch (e: IOException){
            e.printStackTrace()
            println("Ошибка при записи в файл: ${e.message}")
        }
    }

    fun read(): ArrayList<Card>{
        val cards = ArrayList<Card>()
        try {
            val cardsData = context.openFileInput(filename).bufferedReader().use { reader->
                reader.readLines()
            }
            for ( i in cardsData.indices step 3){
                val disc: List<String> =  cardsData[i+2].split(" ")
                val card = Card(
                    cardsData[i+1],
                    cardsData[i],
                    disc[0]=="1",
                    if (disc[0] == "1")
                        disc[1]
                    else
                        null,
                )
                cards.add(card)
            }

        }
        catch (e: IOException){
            e.printStackTrace()
            println("Ошибка при чтении файла: ${e.message}")
        }
        return cards
    }

    fun delete(position: Int){
        try {
            val cardsData = context.openFileInput(filename).bufferedReader().use { reader ->
                reader.readLines()
            }
            val newCardsData = cardsData.filterIndexed { index, _ -> index !in position*3..position*3+2 }
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.bufferedWriter().use { writer ->
                        for(lines in newCardsData){
                            writer.write(lines)
                            writer.newLine()
                        }
                    }
                }
        }
        catch (e: IOException){
            e.printStackTrace()
            println("Ошибка при удалении из файла: ${e.message}")
        }
    }

    fun edit(position: Int, card: Card){
        try {
            val cardsData = context.openFileInput(filename).bufferedReader().use { reader ->
                reader.readLines()
            }.toMutableList()

            cardsData[position*3] = card.shtr.toString()
            cardsData[position*3+1] = card.name.toString()
            if (card.isDisc==true)
                cardsData[position*3+2] = "1 ${card.disc}"
            else
                cardsData[position*3+2] = "0"

            context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.bufferedWriter().use { writer ->
                    for(lines in cardsData){
                        writer.write(lines)
                        writer.newLine()
                    }
                }
            }
        }
        catch (e: IOException){
            e.printStackTrace()
            println("Ошибка при редактировании файла: ${e.message}")
        }
    }

}