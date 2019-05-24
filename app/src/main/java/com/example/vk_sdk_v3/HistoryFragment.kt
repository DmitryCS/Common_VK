package com.example.vk_sdk_v3

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.content_main.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.arch.persistence.room.Room
import android.util.Base64
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class HistoryFragment: Fragment() {
    private var adapter: MyRecyclerViewAdapterHistory? = null
    private var tv_res: TextView? = null
    private var recyclerViewHistory: RecyclerView? = null

    val coms = arrayListOf<Common>()
    var mIds = arrayListOf<Pair<Int,Int>>()
    var mData = arrayListOf<Pair<String,String>>()
    var mPhoto = arrayListOf<Pair<Bitmap,Bitmap>>()
    var mCommon = arrayListOf<Int>()
    var mForG = arrayListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, conteinerForAll, false)
    }

    override fun onStart() {
        super.onStart()
        MainActivity.database =  Room.databaseBuilder(this.context!!, MyDatabase2::class.java, "we-need-db16").build()

        recyclerViewHistory = view?.findViewById<View>(R.id.rvHistory) as RecyclerView
        recyclerViewHistory!!.layoutManager = LinearLayoutManager(this.context)
        adapter = MyRecyclerViewAdapterHistory(this.context!!, mIds, mData, mPhoto, mCommon, mForG)
        recyclerViewHistory!!.adapter = adapter

        val tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.loading)
        var sizeComs: Int
        doAsync {
            val coms = MainActivity.database?.commonDao()?.getAll()
            sizeComs = coms!!.size
            uiThread {
                if(sizeComs == 0)
                    view?.findViewById<TextView>(R.id.tv_empty_history)!!.visibility = View.VISIBLE
                mIds.clear()
                mCommon.clear()
                mData.clear()
                mForG.clear()
                mPhoto.clear()
                for(j: Int in 0 until sizeComs){
                    mIds.add(Pair(0,0))
                    mCommon.add(0)
                    mData.add(Pair("",""))
                    mForG.add(0)
                    mPhoto.add(Pair(tempBitmap,tempBitmap))
                }
                if (coms != null) {
                    for(i in 0 until coms.size){
                        mIds[i] = Pair(coms?.get(i)!!.vkId1, coms?.get(i)!!.vkId2)
                        mCommon[i] = coms?.get(i)!!.common
                        mForG[i] = coms?.get(i)!!.ForG
                        doAsync {
                            var person1: Person? = MainActivity.database?.personDao()?.getObjByVkid(mIds[i].first)
                            var person2: Person? = MainActivity.database?.personDao()?.getObjByVkid(mIds[i].second)
                            uiThread {
                                mData[i] = Pair(person1?.name.toString(), person2?.name.toString())
                                doAsync {
                                    var imageString = person1?.photo
                                    var imageBytes = Base64.decode(imageString, Base64.DEFAULT)
                                    var decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes!!.size)
                                    var imageString2 = person2?.photo
                                    var imageBytes2 = Base64.decode(imageString2, Base64.DEFAULT)
                                    var decodedImage2 = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2!!.size)
                                    uiThread {
                                        mPhoto[i] = Pair(decodedImage, decodedImage2)
                                        adapter?.notifyItemChanged(i)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        MainActivity.database!!.close()
        adapter!!.notifyDataSetChanged()
    }
}