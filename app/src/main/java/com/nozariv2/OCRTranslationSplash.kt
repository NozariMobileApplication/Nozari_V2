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
import org.ligboy.translate.Translate
import java.io.IOException

class OCRTranslationSplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrtranslation_splash)

        Log.i("URI", intent.getStringExtra("IMAGE_URI"))

        val imageURI = intent.getStringExtra("IMAGE_URI")

        if (imageURI!=null){
            launchOCRCameraIntent()
        }
    }

    private fun managedImageFromUri(selectedImage: Uri) : Bitmap? {
        var bitmap : Bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
            return bitmap

        }catch (e:Exception){
            //TODO
            e.printStackTrace()
            return null
        }
    }

    private fun onDeviceRecognizeText(bitmap: Bitmap?) {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)

/*        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer
        val options = FirebaseVisionCloudDocumentRecognizerOptions.Builder()
            .setLanguageHints(listOf("en"))
            .build()
        val detector = FirebaseVision.getInstance()
            .getCloudDocumentTextRecognizer(options)*/

        val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().cloudTextRecognizer
        var result: Task<FirebaseVisionText> = detector.processImage(image)
            .addOnSuccessListener {

                Log.i("text", it.text)
                GlobalScope.launch{ makeTranslationRequest(it.text) }

            }
            .addOnFailureListener { p0 -> Toast.makeText(this@OCRTranslationSplash, p0.message, Toast.LENGTH_LONG).show()
                Log.i("text", p0.message)}
    }

    suspend fun makeTranslationRequest(text: String){

        val newText = text.replace("\n", " ")
        val newNewtext = newText.replace(".", "_")

        //val result = slowFetch(newNewtext)

        withContext(Dispatchers.Main){
            launchOCRCameraIntent()
        }
    }

    suspend fun makeTranslationRequestNew(text: String){
        //val translate = Translate.Builder().build()
         GlobalScope.launch{ withContext(Dispatchers.IO){

            try {

               // translate.refreshTokenKey()
                //val translateResult = translate.translate("", "en", "zu")

                launchOCRCameraIntent()

            } catch (e: Exception){

                Log.i("exception", e.toString())

            }
        } }

    }

/*    private fun slowFetch(text: String): String{
        val trans = Translator()
        Log.i("info", "In slow fetch!")

        var langCode = "zu"

        if(intent.getStringExtra("language_selection")!=null){
            langCode = intent.getStringExtra("language_selection")
        }

        //return trans.callUrlAndParseResult("en", langCode, text)
    }*/

    private fun launchOCRCameraIntent(){
        val intent = Intent(this, ImageTester::class.java).apply {
            //putExtra("translated_text", text)
            data = (intent.data)
        }
        startActivity(intent)
    }

    private fun launchOCRCameraIntentOld(text: String){
        val intent = Intent(this, OCRCameraNew::class.java).apply {
            putExtra("translated_text", text)
            this.setData(intent.data)
        }
        startActivity(intent)
    }
}
