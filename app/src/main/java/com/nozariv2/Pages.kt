package com.nozariv2

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.nozariv2.database.adapters.PageListAdapter
import com.nozariv2.database.roomdatabase.BookRoomDatabase
import com.nozariv2.database.viewModels.PageViewModel
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*


class Pages : AppCompatActivity() {

    private lateinit var pageViewModel: PageViewModel
    lateinit var mainImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_pages)

        mainImageView = findViewById(R.id.page_image)
        val bookId = intent.getStringExtra("BOOK_ID").toInt()

        val recyclerView = findViewById<RecyclerView>(R.id.pages_recyclerview)
        val adapter = object : PageListAdapter(this){
            override fun onPictureClick() { //override the abstract method
//                mainImageView.setImageURI(Uri.parse( this.pages[this.position].uri))
                val adapter = this
                doAsync { mainImageView.setImageURI(Uri.parse( adapter.pages[adapter.position].uri)) }
            }}
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        pageViewModel.allPages.observe(this, Observer { pages ->
            // Update the cached copy of the words in the adapter.
            pages?.let { adapter.setPages(it) ;
                if (adapter.pages.isNotEmpty()) mainImageView.setImageURI(Uri.parse(adapter.pages[0].uri)) }
        })

        pageViewModel.filter(bookId)

        // Update book access date
        val booksDoa = BookRoomDatabase.getDatabase(this).bookDoa()
        booksDoa.updateAccessDate(bookId,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString() )

//        while (adapter.set==false) // Wait for list to be gathered
        if (adapter.pages.isNotEmpty()) mainImageView.setImageURI(Uri.parse(adapter.pages[0].uri))
//        Toast.makeText(this, adapter.pages.size.toString(), Toast.LENGTH_LONG).show()
    }

    fun getPages(): Pages {
        val page = this
        return page
    }

    fun getImageView(): ImageView? {
        val imageViewForPage = findViewById<ImageView>(R.id.page_image)
        return imageViewForPage
    }
}
