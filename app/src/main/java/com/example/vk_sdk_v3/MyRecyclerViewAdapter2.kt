package com.example.vk_sdk_v3

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MyRecyclerViewAdapter2// data is passed into the constructor
internal constructor(val context: Context,
                     data: ArrayList<String>, photos: ArrayList<Bitmap>, val ids: ArrayList<Int>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter2.ViewHolder>() {

    private var mData = arrayListOf<String>()
    private var mPhoto = arrayListOf<Bitmap>()
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null



    init {
        this.mInflater = LayoutInflater.from(context)
        this.mData = data
        this.mPhoto = photos
    }


    override fun getItemCount() =  mData.size

    fun setItems(items: ArrayList<String>) {
        this.mData = items
    }
    fun setItems2(items: ArrayList<Bitmap>) {
        this.mPhoto = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = mData[position]
        holder.myTextView.text = animal
        if(position < mPhoto.size)
            holder.myImageView.setImageBitmap(mPhoto[position])
        else
            holder.myImageView.setImageBitmap(null)
        holder.holderId = position
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var holderId: Int?=null
        internal var myTextView: TextView
        internal var myImageView: ImageView

        init {
            myTextView = itemView.findViewById(R.id.info_text)
            myImageView = itemView.findViewById(R.id.iv_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(view, getAdapterPosition())
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/club${ids[holderId!!]}"))
            context.startActivity(intent)
        }
    }

    internal fun getItem(id: Int): String {
        return mData[id].toString()
    }

    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}
