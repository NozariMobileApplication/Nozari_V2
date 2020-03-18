package com.nozariv2.database.adapters


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.R
import com.nozariv2.database.tables.Page
import org.jetbrains.anko.doAsync


abstract class PageListAdapter internal constructor(context: Context) : RecyclerView.Adapter<PageListAdapter.PageViewHolder>(){

    lateinit var holder: PageViewHolder
    var position: Int=0

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var pages = emptyList<Page>() // Cached copy of books
    val context = context
    lateinit var parent: ViewGroup

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageItemView: TextView = itemView.findViewById(R.id.recyclerPageView_Text)
        val pageIcon: ImageView = itemView.findViewById(R.id.recyclerPageView_Icon)
//        val mainPageImage: ImageView = mainView.findViewById(R.id.page_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        this.parent = parent
        val itemView = inflater.inflate(R.layout.recyclerview_page_item, parent, false)
//        val mainView = inflater.inflate(R.layout.activity_pages,parent,false)
        return PageViewHolder(itemView)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val current = pages[position]
        holder.pageItemView.text = current.id.toString()


        doAsync {
            val bitmap = BitmapFactory.decodeFile(current.uri)
            var resized = Bitmap.createScaledBitmap(bitmap, 150, 200, false)
            holder.pageIcon.setImageBitmap(resized)
        }

        holder.itemView.setOnClickListener(){
            this.holder=holder
            this.position=position
            holder.pageItemView.text=current.createDate
            onPictureClick()
//
//            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_pages,parent,false)
//            val mainPageImage: ImageView = itemView.findViewById(R.id.page_image)
//            mainPageImage.setImageURI(null)
//            mainPageImage.setImageURI(Uri.parse(current.uri))
//            mainPageImage.setBackgroundColor(Color.BLACK)

        }
    }

    internal fun setPages(pages: List<Page>) {
        this.pages = pages
        notifyDataSetChanged()
    }

    override fun getItemCount() = pages.size

    abstract fun onPictureClick()

}

