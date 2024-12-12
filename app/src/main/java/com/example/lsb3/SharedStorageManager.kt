package com.example.lsb3

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream

class SharedStorageManager(private val context: Context) {

    private val filename = "cards.pdf"

    fun saveToPdf(cards: ArrayList<Card>){
        try {
            val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), filename)
            val writer = PdfWriter(FileOutputStream(filePath))
            val pdf = PdfDocument(writer)
            val document = Document(pdf)
            for (i in 0..<cards.size){
                document.add(Paragraph(cards[i].name))
                if (cards[i].isDisc==true){
                    document.add(Paragraph("Discount    " + cards[i].disc + "%"))
                }
                else{
                    document.add(Paragraph("Points-based"))
                }
                val byteArrayOutputStream = ByteArrayOutputStream()
                cards[i].shtrImg?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray())
                val image = Image(imageData)
                image.scaleToFit(100F, 70F)
                document.add(image)
                document.add(Paragraph(cards[i].shtr))
                document.add(Paragraph("----------------------------"))
            }
            document.close()
            Toast.makeText(context, "Ваши карты успешно сохранены в файл cards.pdf", Toast.LENGTH_LONG).show()
        } catch (e: Exception){
            e.printStackTrace()
            println("Ошибка при записи в файл: ${e.message}")
        }
    }
}