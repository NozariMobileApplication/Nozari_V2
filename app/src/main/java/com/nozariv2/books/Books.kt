package com.nozariv2.books

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.Home
import com.nozariv2.OCRTranslationSplash
import com.nozariv2.R
import com.nozariv2.Wallet
import com.nozariv2.authentication.Login
import com.nozariv2.database.adapters.BookListAdapter
import com.nozariv2.database.tables.Book
import com.nozariv2.database.viewModels.BookViewModel
import com.nozariv2.databinding.ActivityBooksBinding
import com.nozariv2.databinding.ActivitySelectbookBinding
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Books : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBooksBinding

    private lateinit var bookViewModel: BookViewModel
    private val newBookActivityRequestCode = 1
    val fAuth = FirebaseAuth.getInstance()


    lateinit var user: User
    var mainViewModel: UsersViewModel? = null


    lateinit var snapsCounter: TextView
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_books)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = getString(R.string.BOOKS)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        mainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        loadUserData()

        val fab = findViewById<FloatingActionButton>(R.id.book_newbook_fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewBook::class.java)
            startActivityForResult(intent, newBookActivityRequestCode)
        }



        val recyclerView = findViewById<RecyclerView>(R.id.book_recyclerview)
        val adapter = BookListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager= GridLayoutManager(this,3)

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        bookViewModel.allbooks.observe(this, Observer { books ->
            // Update the cached copy of the words in the adapter.
            books?.let { adapter.setBooks(it) }
        })

        binding.booksContent.booksSearchbar.addTextChangedListener { text -> bookViewModel.searchNameChanged(text.toString()) }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("user", user)
        Log.i("onBack", user.tokens.toString())
        startActivity(intent)
    }

    fun loadUserData(){
        val serializableUser = intent.getSerializableExtra("user")
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

/*        user = (intent.getSerializableExtra("user") as? User)!!
        if(user!=null){
            Log.i("LOG", "User not null!")
            snapsCounter = findViewById(R.id.top_bar_camera_amount)
            snapsCounter.text = user.tokens.toString()
        } else {
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
        }*/
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

    fun startWalletIntent(view: View){
        val intent = Intent(this, Wallet::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

//    Creating a new book
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newBookActivityRequestCode && resultCode == Activity.RESULT_OK) {
            var userId: String=""
            //var createDate = "Test Date"
//            var createDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
            var createDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString()
            Toast.makeText(applicationContext,createDate, Toast.LENGTH_LONG).show()


            var bookName: String=""
            var uri:String=""

            data?.getStringExtra(NewBook.BOOKNAME_REPLY)?.let {
                bookName = it
            }
            data?.getStringExtra(NewBook.USERID_REPLY)?.let {
                userId = it
            }

            data?.getStringExtra(NewBook.URI_REPLY)?.let {
                uri = it
            }

            if (!userId.equals("")&&!bookName.equals(""))
            {
                var book = Book(0, userId, (createDate),(createDate), bookName,uri)
                bookViewModel.insert(book)
                Toast.makeText(applicationContext,R.string.book_created_string, Toast.LENGTH_LONG).show()
            }
        }
        else if(requestCode == newBookActivityRequestCode)
        {
            Toast.makeText(applicationContext,R.string.new_book_error_string, Toast.LENGTH_LONG).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, Home::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }
            R.id.nav_books -> {
                //Do nothing, here already
            }
            R.id.nav_wallet -> {
                val intent = Intent(this, Wallet::class.java)
                intent.putExtra("user", user)
                //fake push
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
