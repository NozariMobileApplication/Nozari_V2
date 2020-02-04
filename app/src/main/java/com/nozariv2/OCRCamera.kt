package com.nozariv2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import kotlinx.android.synthetic.main.activity_ocrcamera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.media.ExifInterface
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

class OCRCamera : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrcamera)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

/*        val imageURI = intent.getStringExtra("IMAGE_URI")
        val actualURI = intent.data

        Log.i("Info11", "${actualURI}")

        Log.i("Info", "${imageURI}")*/

        val imageURI = intent.data

        Log.i("info", "${imageURI}")

        val imageView = findViewById<ImageView>(R.id.ocrCameraImageView)


/*
        val imageView = findViewById<ImageView>(R.id.ocrCameraImageView).apply {
            this.setImageURI(Uri.parse(imageURI))
        }
*/



/*        val rotateImage = getCameraPhotoOrientation(
            this, imageURI,
            Uri.parse(imageURI).toString()
        )*/

/*
        val imageFile = File(imageURI!!.path)

*/


        val ei = ExifInterface(imageURI!!.toString())
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED)

        val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)

        val rotatedBitmap : Bitmap

        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(imageBitmap, 90f).also { Log.i("info", orientation.toString()) }
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(imageBitmap, 180f).also { Log.i("info", orientation.toString()) }
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(imageBitmap, 270f).also { Log.i("info", orientation.toString()) }
            else -> {
                rotatedBitmap = imageBitmap
                Log.i("info", orientation.toString())
            }
        }

        imageView.setImageBitmap(rotatedBitmap)

        if (imageURI != null) {
            managedImageFromUri(imageURI)
        }



/*        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra("Extra_Message")

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.ocrCameraTextView).apply {
            text = message
        }*/

    }

    private fun rotateImage(source: Bitmap, angle: Float) : Bitmap {

        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)

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
        val textView =  findViewById<TextView>(R.id.ocrCameraTextView)
        val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        var result: Task<FirebaseVisionText> = detector.processImage(image)
            .addOnSuccessListener {

                Log.i("text", it.text)
                Toast.makeText(this@OCRCamera, it.text, Toast.LENGTH_LONG).show()

                textView.text = it.text

                /*p0 ->
                for(block: FirebaseVisionText.TextBlock in p0!!.textBlocks){
                    var boundingBox: Rect = block.boundingBox!!
                    var cornerPoints: Array<Point> = block.cornerPoints!!
                    text+=block.text
                }
                tvOutput.text = text*/
            }
            .addOnFailureListener { p0 -> Toast.makeText(this@OCRCamera, p0.message, Toast.LENGTH_LONG).show() }
    }

    fun getCameraPhotoOrientation(context: Context, imageUri: Uri,imagePath: String): Int {
        var rotate = 0
        try {
            context.getContentResolver().notifyChange(imageUri, null)
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }

            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    val REQUEST_TAKE_PHOTO = 1

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }

        Log.i("Info", "${currentPhotoPath}")

    }

}
