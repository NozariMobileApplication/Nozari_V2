package com.nozariv2

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.authentication.Login
import com.nozariv2.books.Books
import technolifestyle.com.imageslider.FlipperLayout
import technolifestyle.com.imageslider.FlipperView
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformer
import java.io.File

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var snapsCounter: TextView
    lateinit var navView: NavigationView
    lateinit var user: User

    var mainViewModel: UsersViewModel? = null

    private lateinit var flipperLayout: FlipperLayout

    val fAuth = FirebaseAuth.getInstance()
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

        user = (intent.getSerializableExtra("user") as? User)!!
        snapsCounter = findViewById(R.id.top_bar_camera_amount)
        snapsCounter.text = user.tokens.toString()

        mainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        /*        mainViewModel!!.getUser(fAuth.currentUser!!.uid).observe(this, Observer { user ->

            Log.i("USRR", user.toString())

            if(user.fullName.equals("Failed")){
                Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                userConnected = false
            } else {
                userConnected = true
            }
            this.user = user

            //mainViewModel!!.useUserToken(fAuth.currentUser!!.uid, user.tokens!!)
        })

        mainViewModel!!.getUser(fAuth.currentUser!!.uid).observe(this, Observer { user ->

            Log.i("USRT", user.tokens!!.toString())

        })

        mainViewModel!!.useUserToken(fAuth.currentUser!!.uid).observe(this, Observer { bool ->

            if(bool){
                Log.i("USRT", "Transaction success")
            } else {
                Log.i("USRT", "Transaction failed")
            }

        })
        pullUserDataAndUpdate(mainViewModel!!)*/

        //Log.i("USR", user.toString())
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
        //intent.removeExtra("user")
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.i("LOG", "onNewIntent!")

        //now getIntent() should always return the last received intent
    }
    fun loadUserData(){
        val serializableUser = intent.getSerializableExtra("user")
        Log.i("User", "in loadUserData()")

        if(serializableUser==null){
            Log.i("LOG", "User null!")
            mainViewModel!!.getUser(fAuth.currentUser!!.uid).observe(this, Observer {
                if(it.fullName.equals("Failed")){
                    Log.i("LOG", "in failed" )
                    Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                    user = it
                    fAuth.signOut()
                    finish()
                    startActivity(Intent(this, Login::class.java))
                    Log.i("LOG", "Signing out user, failed to load data" )
                } else {
                    user = it
                    snapsCounter = findViewById(R.id.top_bar_camera_amount)
                    snapsCounter.text = user.tokens.toString()
                }
            })
        } else {
            Log.i("LOG", "User not null!")
            user = (serializableUser as? User)!!
            snapsCounter = findViewById(R.id.top_bar_camera_amount)
            snapsCounter.text = user.tokens.toString()
        }
    }

    fun pullUserDataAndUpdate(viewModel : UsersViewModel){
        viewModel.getUser(fAuth.currentUser!!.uid).observe(this, Observer {
            if(user.fullName.equals("Failed")){
                Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                userConnected = false
            } else {
                userConnected = true
                this.user = it
                //change UI elements
                snapsCounter = findViewById(R.id.top_bar_camera_amount)
                snapsCounter.text = user.tokens.toString()
            }
        })
    }

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = Uri.EMPTY

    override fun onBackPressed() {
        Toast.makeText(this,"back key is pressed", Toast.LENGTH_SHORT).show()
    }

    fun startBooksIntent(view: View){
        val intent = Intent(this, Books::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    fun startWalletIntent(view: View){
        val intent = Intent(this, Wallet::class.java)
        intent.putExtra("user", user)
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
                intent.putExtra("user", user)
                startActivity(intent)
            }
            R.id.nav_wallet -> {
                val intent = Intent(this, Wallet::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
            R.id.nav_help -> {
                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_quicklinks -> {
                Toast.makeText(this, "Quick Links clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }R.id.nav_logout -> {
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                fAuth.signOut()
                finish()
                startActivity(Intent(this, Login::class.java).apply {
                    this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
        }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}