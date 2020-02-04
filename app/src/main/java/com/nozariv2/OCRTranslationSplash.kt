package com.nozariv2

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class OCRTranslationSplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrtranslation_splash)

        val imageURI = intent.data

        managedImageFromUri(imageURI!!)
    }

    fun getNewLineIndexes(text: String): ArrayList<Int>{
        val indexes = ArrayList<Int>()
        var startIndex = 0
        while(true){
            var sub = text.substring(startIndex)
            if((text.indexOf("\n", startIndex, true)==-1)){
                Log.i("ÏNDEXSEARCH", "in break condition")
                break
            } else{
                val nearIndex = text.indexOf("\n", startIndex, true)
                Log.i("ÏNDEXSEARCH", "val of nearIndex: ${nearIndex}")
                Log.i("Sub String", text.substring(startIndex))

                indexes.add(nearIndex)
                startIndex = nearIndex+1
                Log.i("ÏNDEXSEARCH", "val of startIndex: ${startIndex}")
            }
        }
        Log.i("ÏNDEXSEARCH", "val of indexes: ${indexes}")

        //Toast.makeText(this@OCRTranslationSplash, "getNewLineIndexes", Toast.LENGTH_LONG).show()

        return indexes
    }

    fun putBackNewLineFromIndexes(indexes: ArrayList<Int>, text: String): String {
        var newText = text
        indexes.forEach{
            newText = newText.replaceRange(it, it+1, "\n")
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

/*        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer
        val options = FirebaseVisionCloudDocumentRecognizerOptions.Builder()
            .setLanguageHints(listOf("en"))
            .build()
        val detector = FirebaseVision.getInstance()
            .getCloudDocumentTextRecognizer(options)*/



        //OLD
        val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        var result: Task<FirebaseVisionText> = detector.processImage(image)
            .addOnSuccessListener {

                Log.i("text", it.text)
                //Toast.makeText(this@OCRTranslationSplash, it.text, Toast.LENGTH_LONG).show()

                //textView.text = it.text
                GlobalScope.launch{ makeTranslationRequest(it.text) }

                //Log.i("text", intent.getStringExtra("language_selection"))

                //launchOCRCameraIntent(it.text)

/*                p0 ->
                for(block: FirebaseVisionText.TextBlock in p0!!.textBlocks){
                    var boundingBox: Rect = block.boundingBox!!
                    var cornerPoints: Array<Point> = block.cornerPoints!!
                    text+=block.text
                }
                tvOutput.text = text*/
            }
            .addOnFailureListener { p0 -> Toast.makeText(this@OCRTranslationSplash, p0.message, Toast.LENGTH_LONG).show()
                Log.i("text", p0.message)}
    }

    suspend fun makeTranslationRequest(text: String){



/*        val indexes = getNewLineIndexes(text)
        var textNew = text.replace("\n", " ", true)
        Log.i("ÏNDEXSEARCH", "val of replaced Text: ${textNew}")*/

/*        val fakeText = "Hello my name is Luca. I am a boy. I play soccer."
        var newFakeText = fakeText.replace("\n", " ")
        val textNew = newFakeText.replace(".", "_")*/

        val newText = text.replace("\n", " ")
        val newNewtext = newText.replace(".", "_")

        val result = slowFetch(newNewtext)

        withContext(Dispatchers.Main){
            Log.i("ÏNDEXSEARCH", "val of before Replaced Text: ${result}")





            launchOCRCameraIntent(result.replace("_", "."))
            //textView.text = putBackNewLineFromIndexes(indexes, result)
        }
    }

    fun launchOCRCameraIntent(text: String){
        val intent = Intent(this, OCRCameraNew::class.java).apply {
            putExtra("translated_text", text)
            this.setData(intent.data)
        }
        startActivity(intent)
    }

    fun slowFetch(text: String): String{
        val trans = Translator()
        Log.i("info", "In slow fetch!")
//        Toast.makeText(this@OCRTranslationSplash, "slow fetch", Toast.LENGTH_LONG).show()

        val fakeText = "Hello my name is Luca. I am a boy. I play soccer."

        var langCode = "zu"

        if(intent.getStringExtra("language_selection")!=null){
            langCode = intent.getStringExtra("language_selection")
        }

        //return trans.callUrlAndParseResult("en", "en", text)
        //return text
        return trans.callUrlAndParseResult("en", langCode, text)
    }
}
