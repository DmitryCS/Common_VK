package com.example.vk_sdk_v3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class Main3ActivityGroups: AppCompatActivity() {
    private var searchField: EditText? = null
    private var data = arrayListOf<String>()
    var dataUrls = arrayListOf<URL>()
    private var ids = arrayListOf<Int>()
    private var ids_temp = arrayListOf<Int>()
    private var ids2 = arrayListOf<Int>()
    private var photos = arrayListOf<Bitmap>()
    var adapter: MyRecyclerViewAdapter2? = null
    private var asyncTasks = arrayListOf<getBitmaps2>()
    private var numberCommonGroups = 0
    var input_id = ""
    var input_id2 = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2_friends)

        findViewById<TextView>(R.id.tv_loading)!!.visibility = View.VISIBLE

        input_id = intent.getStringExtra("id")
        input_id2 = intent.getStringExtra("id2")

        var generatedURL: URL = NetworkUtils.generateURL4(input_id, this.getSharedPreferences("access_token", Context.MODE_PRIVATE)?.getString("token","wtf"))
        var generatedURL2: URL = NetworkUtils.generateURL4(input_id2, this.getSharedPreferences("access_token", Context.MODE_PRIVATE)?.getString("token","wtf"))


        val recyclerView = findViewById<View>(R.id.rvNumbers) as RecyclerView
        val numberOfColumns = 3
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        adapter = MyRecyclerViewAdapter2(this, data, photos, ids)
        recyclerView.adapter = adapter


        VKQueryGroups_3().execute(generatedURL, generatedURL2)
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        for(asyncTask in asyncTasks)
            asyncTask.cancel(true)
    }

    inner class VKQueryGroups_3: AsyncTask<URL, Void, Pair<String?, String?>>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: URL?): Pair<String?,String?> {
            var response: String? = NetworkUtils.getResponseFromURL2(params[0]!!)
            var response2: String? = NetworkUtils.getResponseFromURL2(params[1]!!)

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
                        data.add(tempJsonObject.optString("name", "")
                        )
                        dataUrls.add(URL(tempJsonObject.optString("photo_100", "")))
                        val asyncTask = getBitmaps2(dataUrls.size-1 )
                        asyncTask.execute(dataUrls[dataUrls.size-1])
                        asyncTasks.add(asyncTask)
                    }
                }
                findViewById<TextView>(R.id.tv_loading)!!.visibility = View.GONE
                numberCommonGroups = data.size
                if(numberCommonGroups == 0)
                    findViewById<TextView>(R.id.tv_nothing_found)!!.visibility = View.VISIBLE
                doAsync {
                    val commonBetweenTwo = Common(0, input_id.toInt(), input_id2.toInt(), numberCommonGroups, 1)
                    Log.i("common", commonBetweenTwo.toString())
                    doAsync {
                        MainActivity.database?.commonDao()?.insert(commonBetweenTwo)        //повтор кода
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