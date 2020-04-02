package com.nozariv2

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.cloudtranslate.TranslationRequest
import com.nozariv2.cloudtranslate.TranslationViewModel
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OCRTranslationSplash : AppCompatActivity() {

    var mainViewModel: TranslationViewModel? = null
    var bitmapURI = null
    var fAuth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    var userMainViewModel: UsersViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrtranslation_splash)

        Log.i("URI", intent.getStringExtra("IMAGE_URI"))

        userMainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

/*        val docRef = db.collection("users").document(fAuth.currentUser!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject<User>()
        }*/

        val imageURI = intent.data

            val image: FirebaseVisionImage
            try {
                image = FirebaseVisionImage.fromFilePath(this, imageURI!!)
                ocrImageWithoutLines(image)
                //start display activity, passing the image uri

            } catch (e: IOException) {
                e.printStackTrace()
            }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        Toast.makeText(this,"Please wait...", Toast.LENGTH_SHORT).show()
    }


    fun ocrImageWithoutLines(image: FirebaseVisionImage) {

        //From Google
        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer
        val mutableBitmap = image.bitmap.copy(image.bitmap.config, true)
        if(mutableBitmap==null){
            Log.i("TAG", "img is null")
        }

        val canvas = Canvas(mutableBitmap)

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->

                val resultText = firebaseVisionText.text

                Log.i("Text", firebaseVisionText.text)
                Log.i("isEmpty", "" + firebaseVisionText.text.isEmpty())

                if(firebaseVisionText.text.isEmpty()){

                    Log.i("IN", "Im in here")
                    val path = saveReceivedImage(mutableBitmap, SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()))

                    userMainViewModel!!.useUserToken(fAuth.currentUser!!.uid).observe(this, Observer { transactionSuccessful ->
                        if(transactionSuccessful){
                            val intent = Intent(this, OverlayedPageDisplay::class.java).apply {
                                putExtra("IMAGE_URI", intent.getStringExtra("IMAGE_URI"))
                                putExtra("fpath", path)
                                data = (intent.data)
                                this.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                putExtra("language_selection", intent.getStringExtra("language_selection"))
                            }
                            finish()
                            startActivity(intent)
                        } else {
                            Toast.makeText(this,"Snap transaction failed. Please check your internet connection & try again.", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    })

                } else {

                val trBlocks: ArrayList<String> = ArrayList()
                var transString = ""

                Log.i("Err", "fVT" + firebaseVisionText.blocks.size)
                Log.i("Err", "fVT" + firebaseVisionText.text)

                transString += firebaseVisionText.blocks[0].text

                for (i in 1..firebaseVisionText.blocks.size - 1) {
                    transString += ("{P}" + firebaseVisionText.blocks[i].text)
                }

                Log.i("TS", transString)


                mainViewModel = ViewModelProvider(this).get(TranslationViewModel::class.java)
                val transReq = TranslationRequest(transString, "zu", "en", "text")
                mainViewModel!!.makeTranslationRequest(
                    "AIzaSyBRATktWsGcNiIQJdE-gyS50tIETZLg3aY",
                    transReq
                ).observe(this, Observer { response ->

                    if (response.code() == 200) {

                        Log.i(
                            "TTS",
                            response.body()!!.data.translations[0].translatedText.split("{P} ").toString()
                        )

                        for (blockText in response.body()!!.data.translations[0].translatedText.split(
                            "{P}"
                        )) {
                            Log.i("TR", blockText)
                            trBlocks.add(blockText)
                        }
                        writeTRBlocksOntoBitmap(trBlocks, firebaseVisionText, canvas, mutableBitmap)

                        //val baos = ByteArrayOutputStream()
                        //mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                        //val imageBytes: ByteArray = baos.toByteArray()
                        val path = saveReceivedImage(mutableBitmap, SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()))
                        userMainViewModel!!.useUserToken(fAuth.currentUser!!.uid).observe(this, Observer { transactionSuccessful ->
                            if(transactionSuccessful){
                                val intent = Intent(this, OverlayedPageDisplay::class.java).apply {
                                    putExtra("IMAGE_URI", intent.getStringExtra("IMAGE_URI"))
                                    putExtra("fpath", path)
                                    data = (intent.data)
                                    this.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                    putExtra("language_selection", intent.getStringExtra("language_selection"))
                                }
                                finish()
                                startActivity(intent)
                            } else {
                                Toast.makeText(this,"Snap transaction failed. Please check your internet connection & try again.", Toast.LENGTH_LONG).show()
                                finish()
                            }
                        })
                        //imageView.setImageBitmap(mutableBitmap)
                    } else {
                        Log.i("Tr", "Translation failure")
                        Log.i("R", "" + response.code())
                        Log.i("R", response.message())
                    }
                })

                Log.i("TT", trBlocks.toString())

            }
            }
            .addOnFailureListener { e ->



                //TODO

            }
    }

    fun writeTRBlocksOntoBitmap(trBlocks: ArrayList<String>, fbVisionText: FirebaseVisionDocumentText, canvas: Canvas, bitmap: Bitmap) {

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

        val textSizeList: ArrayList<Float> = ArrayList()
        val longestCharCountList: ArrayList<String> = ArrayList()
        val lineCount: ArrayList<Int> = ArrayList()
        val blockColours: ArrayList<Color> = ArrayList()

        //DETERMINE LONGEST LINE IN EACH BLOCK
        //STORED IN LONGESTCHARCOUNT
        for (i in 0..fbVisionText.blocks.size - 1) {

            //var block = fbVisionText.blocks[i]

            //trBlocks[i]

            val splitLine = trBlocks[i].split("\n")
            var longestCharCount = 0
            var longestLine = ""
            var totalLine = 0

            for (line in splitLine) {

                if (line.length > longestCharCount) {
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

        //determining the fontsize for each textblock & background colour & writing on the blocks
        for (i in 0..fbVisionText.blocks.size - 1) {

            var block = fbVisionText.blocks[i]

            val testTextSize = 70.0

            //TODO boundsWidth and boundsHeight are the same value

            textPaint.setTextSize(testTextSize.toFloat())
            val boundsWidth: Rect = Rect()
            val boundsHeight: Rect = Rect()
            textPaint.getTextBounds(
                longestCharCountList[i],
                0,
                longestCharCountList[i].length,
                boundsWidth
            )
            textPaint.getTextBounds(
                longestCharCountList[i],
                0,
                longestCharCountList[i].length,
                boundsHeight
            )

            // Calculate the desired size as a proportion of our testTextSize.
            val desiredTextSize: Double
            //val desiredTextSizeWidth = testTextSize * block.boundingBox!!.width() / bounds.width()

            //TODO FIGURE OUT WHY DIVIDE BY ZERO, TRY DEBUG THE CALLS SEE WHAT IS ZERO

            Log.i("BW", boundsWidth.width().toString())
            Log.i("BBW", block.boundingBox!!.width().toString())
            Log.i("BH", boundsHeight.height().toString())
            Log.i("BBH", block.boundingBox!!.height().toString())
            Log.i("LCount", lineCount[i].toString())


            Log.i(
                "MinW",
                (block.boundingBox!!.width().toDouble() / boundsWidth.width().toDouble()).toString()
            )
            Log.i(
                "MinH",
                (block.boundingBox!!.height() / boundsHeight.height() * lineCount[i]).toString()
            )

            desiredTextSize = testTextSize * Math.min(
                block.boundingBox!!.width().toDouble() / boundsWidth.width().toDouble(),
                block.boundingBox!!.height().toDouble() / boundsHeight.height().toDouble() * lineCount[i]
            )

            Log.i("TS", desiredTextSize.toString())

            textSizeList.add(i, desiredTextSize.toFloat())

            Log.i("textSize", desiredTextSize.toString())



            val pixel1 = bitmap.getPixel(zeroIfNegative(block.boundingBox!!.left), zeroIfNegative(block.boundingBox!!.top))
            val pixel2 = bitmap.getPixel(zeroIfNegative(block.boundingBox!!.left), zeroIfNegative(block.boundingBox!!.bottom))
            val pixel3 = bitmap.getPixel(zeroIfNegative(block.boundingBox!!.right), zeroIfNegative(block.boundingBox!!.top))
            val pixel4 = bitmap.getPixel(zeroIfNegative(block.boundingBox!!.right), zeroIfNegative(block.boundingBox!!.bottom))

            val R = ((Color.red(pixel1) + Color.red(pixel2) + Color.red(pixel3) + Color.red(pixel4))/4).toInt()
            val G = ((Color.green(pixel1) + Color.green(pixel2) + Color.green(pixel3) + Color.green(pixel4))/4).toInt()
            val B = ((Color.blue(pixel1) + Color.blue(pixel2) + Color.blue(pixel3) + Color.blue(pixel4))/4).toInt()


            rectPaint.apply {
                color = Color.argb(255, R, G, B)
                //color = Color.WHITE
                //Log.i("CLR", Color.argb(1, Color.red(pixel), Color.green(pixel), Color.blue(pixel)))
                isAntiAlias = true
                style = Paint.Style.FILL
                //strokeWidth = 20f
            }

            canvas.drawRect(block.boundingBox!!, rectPaint)

            val splitLine = trBlocks[i].split("\n")

            textPaint.setTextSize(textSizeList.get(i).toFloat())

            var x = block.boundingBox!!.left.toFloat()
            var y = block.boundingBox!!.top.toFloat()
            y += 1 * (textPaint.descent() - textPaint.ascent())

            for (i in 0..splitLine.size - 1) {
                if (!i.equals("\n")) {
                    canvas.drawText(splitLine[i]!!, x, y, textPaint)
                    y += textPaint.descent() - textPaint.ascent()
                }
            }
        }



    }

    fun zeroIfNegative(num : Int) : Int {
        return if(num<=0){
            0
        } else {
            num
        }
    }

    private fun saveReceivedImage(imageBitmap: Bitmap, imageName: String) : String {
        var filePath : String = ""
        try {
            val path = File(this.getFilesDir(), "Pixilate" + File.separator.toString() + "Images")
            if (!path.exists()) {
                path.mkdirs()
            }
            val outFile = File(path, "$imageName.jpeg")
            val outputStream = FileOutputStream(outFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            Log.i("File", outFile.absolutePath)
            outputStream.close()
            filePath = outFile.absolutePath
        } catch (e: FileNotFoundException) {
            filePath = "ERR"
            Log.e("ERR", "Saving received message failed with", e)
        } catch (e: IOException) {
            filePath = "ERR"
            Log.e("ERR", "Saving received message failed with", e)
        }
        return filePath
    }
}