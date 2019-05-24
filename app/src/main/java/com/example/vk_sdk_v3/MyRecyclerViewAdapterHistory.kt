package com.example.vk_sdk_v3

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.R.attr.data



class MyRecyclerViewAdapterHistory// data is passed into the constructor
internal constructor(val context: Context,
                     ids: ArrayList<Pair<Int,Int>>, data: ArrayList<Pair<String,String>>,
                     photos: ArrayList<Pair<Bitmap,Bitmap>>, common: ArrayList<Int>, ForG: ArrayList<Int>) :
    RecyclerView.Adapter<MyRecyclerViewAdapterHistory.ViewHolder>() {

    private var mIds = arrayListOf<Pair<Int,Int>>()
    private var mData = arrayListOf<Pair<String,String>>()
    private var mPhoto = arrayListOf<Pair<Bitmap,Bitmap>>()
    private val mInflater: LayoutInflater
    private var mCommon = arrayListOf<Int>()
    private var mForG = arrayListOf<Int>()
    private var mClickListener: ItemClickListener? = null



    init {
        this.mInflater = LayoutInflater.from(context)
        this.mData = data
        this.mPhoto = photos
        this.mCommon = common
        this.mForG = ForG
        this.mIds = ids
    }


    override fun getItemCount() =  mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = mData[position].first
        val name2 = mData[position].second
        holder.myTextView.text = name
        holder.myTextView2.text = name2
        if(mForG[position] == 0)
            holder.btn.text = "Общие друзья:\n" + mCommon[position].toString()
        else
            holder.btn.text = "Общие группы:\n" + mCommon[position].toString()
        if(position < mPhoto.size) {
            holder.myImageView.setImageBitmap(mPhoto[position].first)
            holder.myImageView2.setImageBitmap(mPhoto[position].second)
        }
        else {
            holder.myImageView.setImageBitmap(null)
            holder.myImageView2.setImageBitmap(null)
        }
        holder.holderId = position
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var holderId: Int?=null
        internal var myTextView: TextView
        internal var myTextView2: TextView
        internal var myImageView: ImageView
        internal var myImageView2: ImageView
        internal var btn: Button

        init {
            myTextView = itemView.findViewById(R.id.info_text_first_history)
            myTextView2 = itemView.findViewById(R.id.info_text_second_history)
            myImageView = itemView.findViewById(R.id.iv_icon_first_history)
            myImageView2 = itemView.findViewById(R.id.iv_icon_second_history)
            btn = itemView.findViewById(R.id.btn_friends_history)
            itemView.setOnClickListener(this)

            btn.setOnClickListener {
                if(mForG[adapterPosition] == 0) {
                    val intent = Intent(context, Main2ActivityFriends/*(searchField?.text.toString())*/::class.java)
                    intent.putExtra("id", mIds[adapterPosition].first.toString())//mIds[holderId!!].first)
                    intent.putExtra("id2", mIds[adapterPosition].second.toString())//mIds[holderId!!].second)
                    context.startActivity(intent)
                }
                else {
                    val intent = Intent(context, Main3ActivityGroups/*(searchField?.text.toString())*/::class.java)
                    intent.putExtra("id", mIds[adapterPosition].first.toString())//mIds[holderId!!].first)
                    intent.putExtra("id2", mIds[adapterPosition].second.toString())//mIds[holderId!!].second)
                    context.startActivity(intent)
                }
            }
            myImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id${mIds[adapterPosition].first}"))
                context.startActivity(intent)
            }
            myImageView2.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id${mIds[adapterPosition].second}"))
                context.startActivity(intent)
            }
        }

        override fun onClick(view: View) {
            if (mClickListener != null) {
                mClickListener!!.onItemClick(view, adapterPosition)
            }
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