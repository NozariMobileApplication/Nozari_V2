package com.nozariv2

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.database.adapters.PageListAdapter
import com.nozariv2.database.viewModels.PageViewModel
import org.jetbrains.anko.doAsync


class Pages : AppCompatActivity() {

    private lateinit var pageViewModel: PageViewModel
    lateinit var mainImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(200);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//        val factory =PageViewModelFactory(this.application, bookId)
//        pageViewModel=ViewModelProviders.of(this,factory).get(PageViewModel::class.java)

        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)
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
}
