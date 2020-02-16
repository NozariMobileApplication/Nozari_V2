package com.nozariv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.database.adapters.PageListAdapter
import com.nozariv2.database.viewModels.PageViewModel
import com.nozariv2.database.viewmodelfactories.PageViewModelFactory


class Pages : AppCompatActivity() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages)

        val bookId = intent.getStringExtra("BOOK_ID").toInt()

        val recyclerView = findViewById<RecyclerView>(R.id.pages_recyclerview)
        val adapter = PageListAdapter(this)
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
}
