package com.nozariv2

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.nozariv2.cloudtranslate.TranslationRequest
import com.nozariv2.cloudtranslate.TranslationViewModel
import kotlinx.coroutines.*
import org.ligboy.translate.Translate
import java.io.IOException
import java.util.stream.IntStream

lateinit var dataLoad: Deferred<String>

class ImageTester : AppCompatActivity() {

    var mainViewModel: TranslationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_tester)

        val imageURI = intent.data
        Log.i("iURI", imageURI.toString())
        val imageView = findViewById<ImageView>(R.id.imageTesterImageView).apply {
            this.setImageURI(imageURI)
        }

        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(this, imageURI!!)
            //ocrImageWithoutLines(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
/*
    fun ocrImageWithoutLines(image: FirebaseVisionImage) {

        //From Google
        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

        val mutableBitmap = image.bitmap.copy(image.bitmap.config, true)

        val canvas = Canvas(mutableBitmap)

        val imageView = findViewById<ImageView>(R.id.imageTesterImageView)
        imageView.setImageBitmap(mutableBitmap)

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->

                val resultText = firebaseVisionText.text

                Log.i("Text", firebaseVisionText.text)

                val trBlocks: ArrayList<String> = ArrayList()
                var transString = ""
                transString += firebaseVisionText.blocks[0].text

                for (i in 1..firebaseVisionText.blocks.size - 1) {
                    transString += ("{P}" + firebaseVisionText.blocks[i].text)
                }

                Log.i("TS", transString)

                mainViewModel = ViewModelProviders.of(this).get(TranslationViewModel::class.java)
                val transReq = TranslationRequest(transString, "af", "en", "text")
                mainViewModel!!.makeTranslationRequest("AIzaSyBRATktWsGcNiIQJdE-gyS50tIETZLg3aY", transReq).observe(this, Observer { response ->

                    if(response.code()==200){

                        Log.i("TTS", response.body()!!.data.translations[0].translatedText.split("{P} ").toString())

                        for(blockText in response.body()!!.data.translations[0].translatedText.split("{P}")){
                            Log.i("TR", blockText)
                            trBlocks.add(blockText)
                        }
                        writeTRBlocksOntoBitmap(trBlocks, firebaseVisionText, canvas)
                        imageView.setImageBitmap(mutableBitmap)
                    } else {
                        Log.i("Tr", "Translation failure")
                        Log.i("R", "" + response.code())
                        Log.i("R", response.message())
                    }
                })

                Log.i("TT", trBlocks.toString())

                }
                    .addOnFailureListener { e ->

                        //TODO

                    }

            }
    }

    fun writeTRBlocksOntoBitmap(trBlocks : ArrayList<String>, fbVisionText : FirebaseVisionDocumentText, canvas : Canvas){

    val TEXT_COLOR = Color.BLACK
    val TEXT_SIZE = 54.0f
    val STROKE_WIDTH = 4.0f

    //From Google
    val textPaint = Paint().apply {
        color = TEXT_COLOR
        textSize = TEXT_SIZE
    }

    val rectPaint =
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
            strokeWidth = 20f
        }

    val textSizeList : ArrayList<Float> = ArrayList()
    val longestCharCountList : ArrayList<String> = ArrayList()
    val lineCount : ArrayList<Int> = ArrayList()

    //DETERMINE LONGEST LINE IN EACH BLOCK
    //STORED IN LONGESTCHARCOUNT
    for (i in 0..fbVisionText.blocks.size-1) {

        //var block = fbVisionText.blocks[i]

        //trBlocks[i]

        val splitLine = trBlocks[i].split("\n")
        var longestCharCount = 0
        var longestLine = ""
        var totalLine = 0

        for (line in splitLine){

            if(line.length > longestCharCount){
                longestCharCount = line.length
                longestLine = line
            }

            totalLine++

            //longestCharCountList[i] = line

            Log.i("Line", line.toString())
            Log.i("LongChar", longestCharCount.toString())
        }
        Log.i("LongString", longestLine.toString())
        longestCharCountList.add(longestLine)
        lineCount.add(totalLine)
    }

    Log.i("LongString", longestCharCountList.toString())

    //determining the fontsize for each textblock & writing on the blocks
    for (i in 0..fbVisionText.blocks.size-1) {

        var block = fbVisionText.blocks[i]

        val testTextSize = 70.0

        //TODO boundsWidth and boundsHeight are the same value

        textPaint.setTextSize(testTextSize.toFloat())
        val boundsWidth : Rect = Rect()
        val boundsHeight : Rect = Rect()
        textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsWidth)
        textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsHeight)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize: Double
        //val desiredTextSizeWidth = testTextSize * block.boundingBox!!.width() / bounds.width()

        //TODO FIGURE OUT WHY DIVIDE BY ZERO, TRY DEBUG THE CALLS SEE WHAT IS ZERO

        Log.i("BW", boundsWidth.width().toString())
        Log.i("BBW", block.boundingBox!!.width().toString())
        Log.i("BH", boundsHeight.height().toString())
        Log.i("BBH", block.boundingBox!!.height().toString())
        Log.i("LCount", lineCount[i].toString())


        Log.i("MinW", (block.boundingBox!!.width().toDouble() / boundsWidth.width().toDouble()).toString())
        Log.i("MinH", (block.boundingBox!!.height() / boundsHeight.height() * lineCount[i]).toString())

        desiredTextSize = testTextSize * Math.min(block.boundingBox!!.width().toDouble() / boundsWidth.width().toDouble(), block.boundingBox!!.height().toDouble() / boundsHeight.height().toDouble() * lineCount[i])

        Log.i("TS", desiredTextSize.toString())

        textSizeList.add(i, desiredTextSize.toFloat())

        Log.i("textSize", desiredTextSize.toString())

        canvas.drawRect(block.boundingBox!!, rectPaint)

        val splitLine = trBlocks[i].split("\n")

        textPaint.setTextSize(textSizeList.get(i).toFloat())

        var x = block.boundingBox!!.left.toFloat()
        var y = block.boundingBox!!.top.toFloat()
        y += 1*(textPaint.descent() - textPaint.ascent())

        for (i in 0..splitLine.size-1) {
            if(!i.equals("\n")) {
                canvas.drawText(splitLine[i]!!, x, y, textPaint)
                y += textPaint.descent() - textPaint.ascent()
            }
        }
    }
}*/
