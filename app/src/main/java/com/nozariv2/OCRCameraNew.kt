package com.nozariv2

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OCRCameraNew : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrcamera_new)

        val imageURI = intent.data
        val imageView = findViewById<ImageView>(R.id.ocrCameraImageView).apply {
            this.setImageURI(imageURI)
        }

        val textView =  findViewById<TextView>(R.id.ocrTextView).apply{
            this.text = intent.getStringExtra("translated_text")
        }

       // managedImageFromUri(imageURI!!)

    }

    fun getNewLineIndexes(text: String): ArrayList<Int>{
        val indexes = ArrayList<Int>()
        var startIndex = 0
        while(true){
            if((text.indexOf("\n", startIndex, true)==-1)){
                Log.i("ÏNDEXSEARCH", "in break condition")
                break
            } else{
                val nearIndex = text.indexOf("\n", startIndex, true)
                Log.i("ÏNDEXSEARCH", "val of nearIndex: ${nearIndex}")
                indexes.add(nearIndex+1)
                startIndex = nearIndex+1
                Log.i("ÏNDEXSEARCH", "val of startIndex: ${startIndex}")
            }
        }
        Log.i("ÏNDEXSEARCH", "val of indexes: ${indexes}")

        return indexes
    }

    fun putBackNewLineFromIndexes(indexes: ArrayList<Int>, text: String): String {
        var newText = text
        indexes.forEach{
            newText = newText.replaceRange(it-1, it, "\n")
        }
        Log.i("ÏNDEXSEARCH", "val of re New Lined Text: ${newText}")
        return newText
    }

    private fun managedImageFromUri(selectedImage: Uri) {
        var bitmap : Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
            //imag.setImageBitmap(bitmap)
            onDeviceRecognizeText(bitmap)
        }catch (e:Exception){
            Log.i("INFO", "what")
            e.printStackTrace()
        }
    }

    private fun onDeviceRecognizeText(bitmap: Bitmap?) {
        var text: String = ""
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
        val textView =  findViewById<TextView>(R.id.ocrTextView)
        val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        var result: Task<FirebaseVisionText> = detector.processImage(image)
            .addOnSuccessListener {

                Log.i("text", it.text)
                Toast.makeText(this@OCRCameraNew, it.text, Toast.LENGTH_LONG).show()

                //textView.text = it.text
                GlobalScope.launch{ makeTranslationRequest(it.text) }


                /*p0 ->
                for(block: FirebaseVisionText.TextBlock in p0!!.textBlocks){
                    var boundingBox: Rect = block.boundingBox!!
                    var cornerPoints: Array<Point> = block.cornerPoints!!
                    text+=block.text
                }
                tvOutput.text = text*/
            }
            .addOnFailureListener { p0 -> Toast.makeText(this@OCRCameraNew, p0.message, Toast.LENGTH_LONG).show() }
    }

    suspend fun makeTranslationRequest(text: String){
        val indexes = getNewLineIndexes(text)
        var textNew = text.replace("\n", " ", true)
        Log.i("ÏNDEXSEARCH", "val of replaced Text: ${textNew}")
        val result = slowFetch(textNew)
        val textView =  findViewById<TextView>(R.id.ocrTextView)

        withContext(Dispatchers.Main){
            Log.i("ÏNDEXSEARCH", "val of before Replaced Text: ${result}")
            textView.text = putBackNewLineFromIndexes(indexes, result)
        }
    }

    fun slowFetch(text: String): String{
        val trans = Translator()
        Log.i("info", "In slow fetch!")
        //Log.i("info", trans.callUrlAndParseResult("en", "zu", text))

        return trans.callUrlAndParseResult("en", "zu", text)
    }


}
