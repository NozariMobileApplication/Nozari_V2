package com.nozariv2

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.books.Books
import com.nozariv2.cloudtranslate.TranslationRequest
import com.nozariv2.cloudtranslate.TranslationViewModel
import com.nozariv2.momo.MomoViewModel
import technolifestyle.com.imageslider.FlipperLayout
import technolifestyle.com.imageslider.FlipperView
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformer
import java.io.File

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var user: User

    var mainViewModel: UsersViewModel? = null

    private lateinit var flipperLayout: FlipperLayout

    val db = Firebase.firestore
    val fAuth = FirebaseAuth.getInstance()
    private var zeroToken = false
    private var userConnected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_home)

        flipperLayout = findViewById(R.id.flipper_layout)
        flipperLayout.addPageTransformer(false, ZoomOutPageTransformer())

        setFlipperImages()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

/*        val docRef = db.collection("users").document(fAuth.currentUser!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject<User>()



            Log.i("Full Name", user!!.fullName)
            Log.i("Email", user!!.email)
            Log.i("Language Selection", user!!.languageSelection)
            Log.i("Phone Number", user!!.phoneNumber)
            Log.i("Tokens", "" + user!!.tokens)
        }*/




        mainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        mainViewModel!!.getUser(fAuth.currentUser!!.uid).observe(this, Observer { user ->

            Log.i("USRR", user.toString())

            if(user.fullName.equals("Failed")){
                Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                userConnected = false
            } else {
                userConnected = true
            }

            if(user.tokens==0){
                zeroToken = true
            } else {
                zeroToken = false
            }

            mainViewModel!!.useUserToken(fAuth.currentUser!!.uid, user.tokens!!)
        })

        mainViewModel!!.getUser(fAuth.currentUser!!.uid).observe(this, Observer { user ->

            Log.i("USRT", user.tokens!!.toString())

        })




        //Log.i("USR", user.toString())


    }

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = Uri.EMPTY

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"back key is pressed", Toast.LENGTH_SHORT).show()
    }

    fun startBooksIntent(view: View){
        val intent = Intent(this, Books::class.java)
        startActivity(intent)
    }

    fun startWalletIntent(view: View){
        val intent = Intent(this, Wallet::class.java)
        startActivity(intent)
    }

    fun setFlipperImages(){

        val flipperViewList: ArrayList<FlipperView> = ArrayList()

        val view1 = FlipperView(baseContext).apply {
            this.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            this.setImage(R.drawable.home_scroller_1) { imageView, image ->
                imageView.setImageDrawable(image as Drawable)
            }
            this.setDescriptionBackgroundAlpha(0f)
        }
        flipperViewList.add(view1)
        val view2 = FlipperView(baseContext).apply {
            this.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            this.setImage(R.drawable.home_scroller_2) { imageView, image ->
                imageView.setImageDrawable(image as Drawable)
            }
            this.setDescriptionBackgroundAlpha(0f)
        }
        flipperViewList.add(view2)
        val view3 = FlipperView(baseContext).apply {
            this.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            this.setImage(R.drawable.home_scroller_3) { imageView, image ->
                imageView.setImageDrawable(image as Drawable)
            }
            this.setDescriptionBackgroundAlpha(0f)
        }
        flipperViewList.add(view3)
        flipperLayout.showInnerPagerIndicator()
        flipperLayout.startAutoCycle()
        flipperLayout.startAutoCycle(5)
        flipperLayout.addFlipperViewList(flipperViewList)
    }



    fun openOCRCameraActivity(view: View){
        //if system os is Marshmallow or Above, we need to request runtime permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Required Permissions Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //image_uri = this.image_uri
        Log.i("TAG", "in open camera in Home")
        Log.i("TAG", image_uri.toString())
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        Log.i("openCamera", "${image_uri}" )
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    fun startImagePicker(view: View){

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

                    val intent = Intent(this, OCRTranslationSplash::class.java).apply {
                        putExtra("IMAGE_URI", fileUri.toString())
                        this.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        this.setData(fileUri)
                    }

                    startActivity(intent)

                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Log.i("ERR",  ImagePicker.getError(data))
                    Toast.makeText(applicationContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                //Do nothing here already
            }
            R.id.nav_books -> {
                val intent = Intent(this, Books::class.java)
                startActivity(intent)
            }
            R.id.nav_wallet -> {
                val intent = Intent(this, Wallet::class.java)
                startActivity(intent)
            }
            R.id.nav_help -> {
                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_quicklinks -> {
                Toast.makeText(this, "Quick Links clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}