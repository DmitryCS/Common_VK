package com.example.vk_sdk_v3

import android.arch.persistence.room.Room
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.vk_sdk_v3.NetworkUtils.Companion.generateURL2
import com.example.vk_sdk_v3.NetworkUtils.Companion.getResponseFromURL2
import com.vk.api.sdk.ui.VKConfirmationActivity.Companion.result
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import android.graphics.BitmapFactory
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException


class Main2ActivityFriends: AppCompatActivity() {
    private var data = arrayListOf<String>()
    var dataUrls = arrayListOf<URL>()
    private var ids = arrayListOf<Int>()
    private var ids_temp = arrayListOf<Int>()
    private var ids2 = arrayListOf<Int>()
    private var photos = arrayListOf<Bitmap>()
    var adapter: MyRecyclerViewAdapter? = null
    private var asyncTasks = arrayListOf<getBitmaps2>()
    private var numberCommonFriends = 0
    var input_id = ""
    var input_id2 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2_friends)

        findViewById<TextView>(R.id.tv_loading)!!.visibility = View.VISIBLE

        MainActivity.database =  Room.databaseBuilder(this, MyDatabase2::class.java, "we-need-db16").build()

         input_id = intent.getStringExtra("id")
         input_id2 = intent.getStringExtra("id2")

        var generatedURL: URL = generateURL2(input_id)
        var generatedURL2: URL = generateURL2(input_id2)

        val recyclerView = findViewById<View>(R.id.rvNumbers) as RecyclerView
        val numberOfColumns = 3
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        adapter = MyRecyclerViewAdapter(this, data, photos, ids)
        recyclerView.adapter = adapter


        VKQueryFriends2().execute(generatedURL, generatedURL2)
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        for(asyncTask in asyncTasks)
            asyncTask.cancel(true)
    }

    inner class VKQueryFriends2: AsyncTask<URL, Void, Pair<String?,String?>>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: URL?): Pair<String?,String?> {
            var response: String? = getResponseFromURL2(params[0]!!)
            var response2: String? = getResponseFromURL2(params[1]!!)

            return Pair(response.toString(), response2.toString())
        }

        override fun onPostExecute(response: Pair<String?,String?>) {
            var firstName: String? = null
            var lastName: String? = null

            if(response != null && !response.equals("")) {
                super.onPostExecute(response)
                var jsonResponse = JSONObject(response.first)
                var jsonObject: JSONObject = jsonResponse.getJSONObject("response")
                var jsonArray: JSONArray = jsonObject.getJSONArray("items")

                var jsonResponse2 = JSONObject(response.second)
                var jsonObject2: JSONObject = jsonResponse2.getJSONObject("response")
                var jsonArray2: JSONArray = jsonObject2.getJSONArray("items")
                var tempJsonObject: JSONObject
                var mIcon11: Bitmap? = null

                for (i: Int in 0 until jsonArray.length()) {
                    tempJsonObject = jsonArray[i] as JSONObject
                    ids_temp.add(tempJsonObject.optInt("id", 3))
                }
                for (i: Int in 0 until jsonArray2.length()) {
                    tempJsonObject = jsonArray2[i] as JSONObject

                    val mid = tempJsonObject.optInt("id", 3)
                    if (mid != 3 && mid in ids_temp) {
                        ids.add(tempJsonObject.optInt("id", 3))
                        data.add(
                            tempJsonObject.optString(
                                "first_name",
                                ""
                            ) + " " + tempJsonObject.optString("last_name", "")
                        )
                        dataUrls.add(URL(tempJsonObject.optString("photo_100", "")))
                        val asyncTask = getBitmaps2(dataUrls.size-1 )
                        asyncTask.execute(dataUrls[dataUrls.size-1])
                        asyncTasks.add(asyncTask)
                    }
                }
                findViewById<TextView>(R.id.tv_loading)!!.visibility = View.GONE
                numberCommonFriends = data.size
                if(numberCommonFriends == 0)
                    findViewById<TextView>(R.id.tv_nothing_found)!!.visibility = View.VISIBLE
                doAsync {
                        val commonBetweenTwo = Common(0, input_id.toInt(), input_id2.toInt(), numberCommonFriends, 0)
                        doAsync {
                            MainActivity.database?.commonDao()?.insert(commonBetweenTwo)
                        }
                }

                adapter?.notifyDataSetChanged()
            } else{

            }


        }
    }


    inner class getBitmaps2(val pos: Int): AsyncTask<URL, Void, Void>(){
        override fun doInBackground(vararg params: URL?): Void? {
            try {
                val url = URL(params[0]?.toString())
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                photos.add(image)
            } catch (e: IOException) {
                println(e)
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            adapter?.notifyItemChanged(pos)
        }
    }

    inner class getBitmaps: AsyncTask<ArrayList<URL>, Void, ArrayList<Bitmap>>(){
        override fun doInBackground(vararg params: ArrayList<URL>?): ArrayList<Bitmap> {
            for(i in 0 until params[0]?.size!!){
                try {
                    val url = URL(params[0]?.get(i)?.toString())
                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    photos.add(image)
                } catch (e: IOException) {
                    println(e)
                }
            }
            return photos
        }

        override fun onPostExecute(result: ArrayList<Bitmap>?) {
            super.onPostExecute(result)
            adapter?.notifyDataSetChanged()
        }
    }
}
