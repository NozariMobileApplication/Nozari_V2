package com.nozariv2.books

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.OCRTranslationSplash
import com.nozariv2.R
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewBook : AppCompatActivity() {

    private lateinit var editWordView: EditText
    private lateinit var coverPage: ImageView
    var uri:String=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        editWordView = findViewById(R.id.book_name_text_input)
        coverPage=findViewById(R.id.add_coverpage_button)
        val button = findViewById<Button>(R.id.create_book_button)

        val selectImageButton = findViewById<ImageView>(R.id.add_coverpage_button)
        selectImageButton.setOnClickListener { startImagePicker() }

        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
                Toast.makeText(applicationContext, "Please enter book name", Toast.LENGTH_SHORT).show()
            }
            else {
                val bookName = editWordView.text.toString()
                //val currentDate = "Test Date"
                val currentDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
                val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                currentFirebaseUser?.let {
                    var uid = currentFirebaseUser.uid
                    replyIntent.putExtra(USERID_REPLY,uid)
                }
                replyIntent.putExtra(URI_REPLY,uri)
                replyIntent.putExtra(BOOKNAME_REPLY, bookName)
                replyIntent.putExtra("date",currentDate)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }

        }
    }

    fun startImagePicker(){

        ImagePicker.with(this)
            //.compress(1024)         //Final image size will be less than 1 MB(Optional)
            //.maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data!!.data
                    //imgProfile.setImageURI(fileUri)

                    Log.i("fURI", fileUri.toString())

                    //You can get File object from intent
                    val file: File? = ImagePicker.getFile(data)

                    //You can also get File Path from intent
                    val filePath: String? = ImagePicker.getFilePath(data)

                    Log.i("fPath", filePath)

                    //TODO
                    //!!! Over here you can use the file path to store the image in internal storage
                    uri=filePath.toString()
                    val bitmap = BitmapFactory.decodeFile(uri)
                    var resized = Bitmap.createScaledBitmap(bitmap, 200, 250, false)
                    coverPage.setImageBitmap(resized)


                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Log.i("ERR",  ImagePicker.getError(data))
                    Toast.makeText(applicationContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

    }

    companion object {
        const val BOOKNAME_REPLY = "com.example.android.bookname.REPLY"
        const val USERID_REPLY = "com.example.android.userid.REPLY"
        const val URI_REPLY = "com.example.android.uri.REPLY"
    }
}
