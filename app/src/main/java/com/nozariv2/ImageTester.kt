package com.nozariv2

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.*
import org.ligboy.translate.Translate
import java.io.IOException
import java.util.stream.IntStream

lateinit var dataLoad: Deferred<String>

class ImageTester : AppCompatActivity() {

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

/*            if(image.bitmap.getWidth() > image.bitmap.getHeight())
            {
                image.bitmap =
            }*/



            ocrImageWithoutLines(image)

        } catch (e: IOException) {
            e.printStackTrace()
        }






    }


    suspend fun translateText(inputText : String, sourceLang : String, targetLang : String) : String? =
        withContext(Dispatchers.IO) {

            val translate = Translate.Builder().logLevel(Translate.LogLevel.BASIC).build()
            var returnString = ""
            try {

                translate.refreshTokenKey()
                val translateResult = translate.translate(inputText, sourceLang, targetLang)
                returnString = translateResult!!.targetText!!

            } catch (e: Exception) {

                Log.i("exception", e.toString())
                //Toast.makeText(applicationContext, "Translation failed", Toast.LENGTH_SHORT).show()
                returnString = "F"


            }
            return@withContext returnString
        }



    fun ocrImageWithoutLines(image: FirebaseVisionImage) {

        //From Google
        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

        val mutableBitmap = image.bitmap.copy(image.bitmap.config, true)

        val canvas = Canvas(mutableBitmap)

        //canvas.drawRect(0f, 50f, 100f, 150f, textPaint)

        val imageView = findViewById<ImageView>(R.id.imageTesterImageView)
        imageView.setImageBitmap(mutableBitmap)

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->

                val resultText = firebaseVisionText.text

                Log.i("Text", firebaseVisionText.text)

                //Make translation request


/*                val trBlocks : ArrayList<String> = ArrayList()


                //Process text onto Bitmap of firebaseVisionText
                writeTextOntoBitmap(trBlocks, firebaseVisionText, canvas)*/

                val trBlocks: ArrayList<String> = ArrayList()
                var transString = ""

                for (i in 0..firebaseVisionText.blocks.size - 1) {
                    transString += ("{P}" + firebaseVisionText.blocks[i].text)
                }

                Log.i("TS", transString)

                //val translatedText = .launch { // we should use IO thread here !

                //}

                GlobalScope.launch {
                    val translatedText = translateText(transString, "en", "en")
                    for(blockText in translatedText!!.split("{P}")){
                        Log.i("TR", blockText)
                        trBlocks.add(blockText)
                    }
                    Log.i("TRB", trBlocks.toString())
                    writeTRBlocksOntoBitmap(trBlocks, firebaseVisionText, canvas)
                    imageView.setImageBitmap(mutableBitmap)

                    // continue processing on the UI thread using videoFile
                }




                Log.i("TT", trBlocks.toString())

                /*dataLoad = GlobalScope.async(Dispatchers.IO) {

                    val translate = Translate.Builder().build()


                    return@async
                }*/


/*                val translate = Translate.Builder().build()*/
                //writeTextOntoBitmap(trBlocks, firebaseVisionText, canvas)



/*                GlobalScope.launch {
                    val operation = async(Dispatchers.IO) {
                        settingsInteractor.getStationSearchCountry().let {
                            countryName = it.name
                        }
                        settingsInteractor.getStationSearchRegion().let {
                            regionName = it.name
                        }
                    }
                    operation.await()*/

                    /*GlobalScope.launch {
                        withContext(Dispatchers.IO) {

                            try {

                                translate.refreshTokenKey()
                                val translateResult = translate.translate(transString, "en", "zu")
                                val trBlocks: ArrayList<String> = ArrayList()

                                Log.i("TR", translateResult!!.targetText)

                                //Process text onto Bitmap of firebaseVisionText
                                //writeTextOntoBitmap(trBlocks, firebaseVisionText, canvas)


                                Log.i("trans", translateResult!!.targetText)

                            } catch (e: Exception) {

                                Log.i("exception", e.toString())

                            }
                        }
                    }*/

                    //imageView.setImageBitmap(image.bitmap)

                }
                    .addOnFailureListener { e ->

                        //TODO

                    }

            }
    }

    fun writeTRBlocksOntoBitmap(trBlocks : ArrayList<String>, fbVisionText : FirebaseVisionDocumentText, canvas : Canvas){

    val TEXT_COLOR = Color.WHITE
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
            color = Color.BLUE
            style = Paint.Style.FILL
            strokeWidth = 20f
        }

    val textSizeList : ArrayList<Float> = ArrayList()
    val longestCharCountList : ArrayList<String> = ArrayList()
    val lineCount : ArrayList<Int> = ArrayList()

    //DETERMINE LONGEST LINE IN EACH BLOCK
    //STORED IN LONGESTCHARCOUNT
    for (i in 0..fbVisionText.blocks.size-1) {

        var block = fbVisionText.blocks[i]

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

    //determining the fontsize for each textblock
    for (i in 0..fbVisionText.blocks.size-1) {

        var block = fbVisionText.blocks[i]

        val testTextSize = 70f

        //TODO boundsWidth and boundsHeight are the same value

        textPaint.setTextSize(testTextSize)
        val boundsWidth : Rect = Rect()
        val boundsHeight : Rect = Rect()
        textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsWidth)
        textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsHeight)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize: Float
        //val desiredTextSizeWidth = testTextSize * block.boundingBox!!.width() / bounds.width()

        //TODO FIGURE OUT WHY DIVIDE BY ZERO, TRY DEBUG THE CALLS SEE WHAT IS ZERO

        Log.i("BW", boundsWidth.width().toString())
        Log.i("BBW", block.boundingBox!!.width().toString())
        Log.i("BH", boundsHeight.height().toString())
        Log.i("BBH", block.boundingBox!!.height().toString())
        Log.i("LCount", lineCount[i].toString())

        desiredTextSize = testTextSize * Math.min(block.boundingBox!!.width() / boundsWidth.width(), block.boundingBox!!.height() / boundsHeight.height() * lineCount[i])

        Log.i("TS", desiredTextSize.toString())

        //textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, bounds)

        //val desiredTextSizeHeight = testTextSize * block.boundingBox!!.height() / bounds.height() / lineCount[i]

        //Log.i("dTSW", desiredTextSizeWidth.toString())
        //Log.i("dTSH", desiredTextSizeHeight.toString())

/*                    desiredTextSize = if(desiredTextSizeHeight<desiredTextSizeWidth){
                        desiredTextSizeHeight
                    } else {
                        desiredTextSizeWidth
                    }*/

        textSizeList.add(i, desiredTextSize)

        Log.i("textSize", desiredTextSize.toString())

        canvas.drawRect(block.boundingBox!!, rectPaint)
        //canvas.drawText(block.text,   left.toFloat(), block.boundingBox!!.bottom.toFloat(), textPaint)

        val splitLine = trBlocks[i].split("\n")

        textPaint.setTextSize(textSizeList.get(i).toFloat())


        var x = block.boundingBox!!.left.toFloat()
        var y = block.boundingBox!!.top.toFloat()
        y += 1*(textPaint.descent() - textPaint.ascent())


        for (i in 0..splitLine.size-1) {

            canvas.drawText(splitLine[i]!!, x, y, textPaint)
            y += textPaint.descent() - textPaint.ascent()

        }
    }
}

    fun writeTextOntoBitmap(trBlocks : ArrayList<String>, fbVisionText : FirebaseVisionDocumentText, canvas : Canvas){


        // TODO TODO TODO LINK TRBLOCKS INSTEAD OF fbVISIONTEXT.TEXT
        val TEXT_COLOR = Color.WHITE
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
                color = Color.BLUE
                style = Paint.Style.FILL
                strokeWidth = 20f
            }

        val textSizeList : ArrayList<Float> = ArrayList()
        val longestCharCountList : ArrayList<String> = ArrayList()
        val lineCount : ArrayList<Int> = ArrayList()

        //DETERMINE LONGEST LINE IN EACH BLOCK
        //STORED IN LONGESTCHARCOUNT
        for (i in 0..fbVisionText.blocks.size-1) {

            var block = fbVisionText.blocks[i]

            //trBlocks[i]

            val splitLine = block.text.split("\n")
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

        //determining the fontsize for each textblock
        for (i in 0..fbVisionText.blocks.size-1) {

            var block = fbVisionText.blocks[i]

            val testTextSize = 70f

            textPaint.setTextSize(testTextSize)
            val boundsWidth : Rect = Rect()
            val boundsHeight : Rect = Rect()
            textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsWidth)
            textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, boundsHeight)

            // Calculate the desired size as a proportion of our testTextSize.
            val desiredTextSize: Float
            //val desiredTextSizeWidth = testTextSize * block.boundingBox!!.width() / bounds.width()

            desiredTextSize = testTextSize * Math.min(block.boundingBox!!.width() / boundsWidth.width(), block.boundingBox!!.height() / boundsHeight.height() * lineCount[i])

            //textPaint.getTextBounds(longestCharCountList[i], 0, longestCharCountList[i].length, bounds)

            //val desiredTextSizeHeight = testTextSize * block.boundingBox!!.height() / bounds.height() / lineCount[i]

            //Log.i("dTSW", desiredTextSizeWidth.toString())
            //Log.i("dTSH", desiredTextSizeHeight.toString())

/*                    desiredTextSize = if(desiredTextSizeHeight<desiredTextSizeWidth){
                        desiredTextSizeHeight
                    } else {
                        desiredTextSizeWidth
                    }*/

            textSizeList.add(i, desiredTextSize)

            Log.i("textSize", desiredTextSize.toString())

            canvas.drawRect(block.boundingBox!!, rectPaint)
            //canvas.drawText(block.text,   left.toFloat(), block.boundingBox!!.bottom.toFloat(), textPaint)

            val splitLine = block.text.split("\n")

            textPaint.setTextSize(textSizeList.get(i).toFloat())


            var x = block.boundingBox!!.left.toFloat()
            var y = block.boundingBox!!.top.toFloat()
            y += 1*(textPaint.descent() - textPaint.ascent())


            for (i in 0..splitLine.size-1) {

                canvas.drawText(splitLine[i]!!, x, y, textPaint)
                y += textPaint.descent() - textPaint.ascent()

            }
        }
    }

/*    fun createCanvasFromImage(bitmap: Bitmap) : Canvas {





    }*/


    /*fun ocrImageWithLines(image: FirebaseVisionImage){

        //From Google
        val TEXT_COLOR = Color.WHITE
        val TEXT_SIZE = 54.0f
        val STROKE_WIDTH = 4.0f

        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

        val canvas = Canvas(image.bitmap)

        //From Google
        val textPaint = Paint().apply {
            color = TEXT_COLOR
            textSize = TEXT_SIZE
        }

        val rectPaint =
            Paint().apply {
                isAntiAlias = true
                color = Color.BLUE
                style = Paint.Style.FILL
                strokeWidth = 20f
            }

        //canvas.drawRect(0f, 50f, 100f, 150f, textPaint)

        val imageView = findViewById<ImageView>(R.id.imageTesterImageView)
        imageView.setImageBitmap(image.bitmap)



        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->

                val resultText = firebaseVisionText.text

                Log.i("Text", firebaseVisionText.text)

                for (i in 0..firebaseVisionText.blocks.size-1) {

                    var block = firebaseVisionText.blocks[i]

                        // Pick a reasonably large value for the test. Larger values produce
                        // more accurate results, but may cause problems with hardware
                        // acceleration. But there are workarounds for that, too; refer to
                        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
                    val testTextSize = 48f

                        // Get the bounds of the text, using our testTextSize.
                    textPaint.setTextSize(testTextSize);
                    val bounds : Rect = Rect()
                    textPaint.getTextBounds(text, 0, text.length(), bounds)

                        // Calculate the desired size as a proportion of our testTextSize.
                    float desiredTextSize = testTextSize * desiredWidth / bounds.width()


                    paint.setTextSize(desiredTextSize);



                    val blockText = block.text
                    val blockConfidence = block.confidence
                    val blockRecognizedLanguages = block.recognizedLanguages
                    val blockFrame = block.boundingBox

                    canvas.drawRect(block.boundingBox!!, rectPaint)
                    //canvas.drawText(block.text,   left.toFloat(), block.boundingBox!!.bottom.toFloat(), textPaint)

                    val splitLine = block.text.split("\n")

*//*                    val x = block.boundingBox!!.left.toFloat()
                    var y = block.boundingBox!!.top.toFloat()




                    if(i!=firebaseVisionText.blocks.size-1){
                        y += 1*(textPaint.descent() - textPaint.ascent())
                    }


                    for (i in 0..splitLine.size-1) {

                        canvas.drawText(splitLine[i]!!, x, y, textPaint)
                        y += textPaint.descent() - textPaint.ascent()


                        //y -= //I need to figure out this value to determine the y amount
                    }*//*

                    var a = block.boundingBox!!.left.toFloat()
                    var b = block.boundingBox!!.top.toFloat()

                    if(i!=firebaseVisionText.blocks.size-1){
                        b += 1*(textPaint.descent() - textPaint.ascent())
                    }


                    for (i in 0..splitLine.size-1) {

                        canvas.drawText(splitLine[i]!!, a, b, textPaint)
                        b += textPaint.descent() - textPaint.ascent()


                        //y -= //I need to figure out this value to determine the y amount
                    }

*//*                    val m = block.boundingBox!!.left.toFloat()
                    var n = block.boundingBox!!.top.toFloat()

                    if(i!=firebaseVisionText.blocks.size-1){
                        n += 3*(textPaint.descent() - textPaint.ascent())
                    }


                    for (i in 0..splitLine.size-1) {

                        canvas.drawText(splitLine[i]!!, m, n, textPaint)
                        n += textPaint.descent() - textPaint.ascent()


                        //y -= //I need to figure out this value to determine the y amount
                    }*//*

                    //imageView.setImageBitmap(image.bitmap)


*//*                    for (paragraph in block.paragraphs) {
                        val paragraphText = paragraph.text
                        val paragraphConfidence = paragraph.confidence
                        val paragraphRecognizedLanguages = paragraph.recognizedLanguages
                        val paragraphFrame = paragraph.boundingBox

                        canvas.drawRect(block.boundingBox!!, textPaint2)

                        imageView.setImageBitmap(image.bitmap)*//*

*//*                        for (word in paragraph.words) {
                            val wordText = word.text
                            val wordConfidence = word.confidence
                            val wordRecognizedLanguages = word.recognizedLanguages
                            val wordFrame = word.boundingBox



                            for (symbol in word.symbols) {
                                val symbolText = symbol.text
                                val symbolConfidence = symbol.confidence
                                val symbolRecognizedLanguages = symbol.recognizedLanguages
                                val symbolFrame = symbol.boundingBox
                            }
                        }*//*
                    }

                imageView.setImageBitmap(image.bitmap)

                }






            .addOnFailureListener { e ->





            }

    }*/




