package com.nozariv2

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.nozariv2.database.adapters.PageListAdapter
import com.nozariv2.database.viewModels.PageViewModel
import org.jetbrains.anko.doAsync


class Pages : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var pageViewModel: PageViewModel
    lateinit var mainImageView: ImageView

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

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

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(200);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//        val factory =PageViewModelFactory(this.application, bookId)
//        pageViewModel=ViewModelProviders.of(this,factory).get(PageViewModel::class.java)

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        pageViewModel.allPages.observe(this, Observer { pages ->
            // Update the cached copy of the words in the adapter.
            pages?.let { adapter.setPages(it) }
        })

        pageViewModel.filter(bookId)

    }

    fun getPages(): Pages {
        val page = this
        return page
    }

    fun getImageView(): ImageView? {
        val imageViewForPage = findViewById<ImageView>(R.id.page_image)
        return imageViewForPage
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_messages -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_friends -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_update -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
