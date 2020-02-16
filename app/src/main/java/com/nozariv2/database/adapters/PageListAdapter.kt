package com.nozariv2.database.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.R
import com.nozariv2.database.tables.Page
import java.io.File


class PageListAdapter internal constructor(context: Context) : RecyclerView.Adapter<PageListAdapter.PageViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var pages = emptyList<Page>() // Cached copy of books

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pageItemView: TextView = itemView.findViewById(R.id.recyclerPageView_Text)
        val pageIcon: ImageView = itemView.findViewById(R.id.recyclerPageView_Icon)
//        val mainPageImage: ImageView = mainView.findViewById(R.id.page_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_page_item, parent, false)
//        val mainView = inflater.inflate(R.layout.activity_pages,parent,false)
        return PageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val current = pages[position]
        holder.pageItemView.text = current.id.toString()

        val bitmap = MediaStore.Images.Media.getBitmap(holder.pageIcon.context.getContentResolver(), Uri.parse(current.uri))
        var resized = Bitmap.createScaledBitmap(bitmap, 150, 200, false)

//        val exif = ExifInterface(File(Uri.parse(current.uri).path))
//        var orientation:Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
//
//        if (orientation==6){
//            val matrix = Matrix()
//            matrix.postRotate(ExifInterface.ORIENTATION_ROTATE_90.toFloat())
//            resized = Bitmap.createBitmap(
//                resized,
//                0,
//                0,
//                resized.getWidth(),
//                resized.getHeight(),
//                matrix,
//                true
//            )
//        }

        holder.pageIcon.setImageBitmap(resized)
//        holder.pageIcon.setImageBitmap(MediaStore.Images.Media.getBitmap(holder.pageIcon.context.getContentResolver(), Uri.parse(current.uri)))


//        holder.itemView.setOnClickListener(){
//            holder.pageItemView.text=current.createDate
////            holder.mainPageImage.setImageURI(Uri.parse(current.uri))
//        }
    }

    internal fun setPages(pages: List<Page>) {
        this.pages = pages
        notifyDataSetChanged()
    }

    override fun getItemCount() = pages.size


}

