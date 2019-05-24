package com.example.vk_sdk_v3

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.vk_sdk_v3.NetworkUtils.Companion.getResponseFromURL
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import android.widget.Toast
import android.arch.persistence.room.Room
import android.content.Context.MODE_PRIVATE

import android.util.Base64
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream


class HomeFragment: Fragment() {
    private var searchField: EditText? = null
    private var searchField2: EditText? = null
    private var searchUsers: Button? = null
    private var searchFriends: Button? = null
    private var searchGroups: Button? = null
    private var result: TextView? = null
    private var icon1: ImageView? = null
    private var icon2: ImageView? = null
    private var textOnIcon1: TextView? = null
    private var textOnIcon2: TextView? = null
    private var toolBar: Toolbar? = null
    private var baos : ByteArrayOutputStream? = null
    private var baos2 : ByteArrayOutputStream? = null

//    private var f = ""
//    private var s = ""
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setRetainInstance(true)
////        if(savedInstanceState != null)
////            f = savedInstanceState!!.getString("id1")
//    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.fragment_home, conteinerForAll, false)
        //v?.findViewById<EditText>(R.id.et_search_field)?.setText(f)
       // Log.d("qqq", f)
    //searchField2?.setText(s)
        return v
    }


    override fun onStart() {
        super.onStart()
        MainActivity.database =  Room.databaseBuilder(this.context!!, MyDatabase2::class.java, "we-need-db16").build()

        searchField = view?.findViewById(R.id.et_search_field)
        searchField2 = view?.findViewById(R.id.et_search_field2)
        searchUsers = view?.findViewById(R.id.b_GetUsers)
        searchFriends = view?.findViewById(R.id.b_GetFriends)
        searchGroups = view?.findViewById(R.id.b_GetGroups)
        icon1 = view?.findViewById(R.id.iv_icon_first)
        icon2 = view?.findViewById(R.id.iv_icon_second)
        textOnIcon1 = view?.findViewById(R.id.info_text_first)
        textOnIcon2 = view?.findViewById(R.id.info_text_second)
        toolBar = view?.findViewById(R.id.toolbar_)

        if(searchField?.text.toString() != "") {
            var generatedURL: URL = NetworkUtils.generateURL(searchField?.text.toString())
            getPhotoURL(icon1, textOnIcon1).execute(generatedURL)
        }
        if(searchField2?.text.toString() != "") {
            var generatedURL: URL = NetworkUtils.generateURL(searchField2?.text.toString())
            getPhotoURL(icon2, textOnIcon2).execute(generatedURL)
        }

        icon1?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id${searchField?.text.toString()}"))
            this.startActivity(intent)
        }
        icon2?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/id${searchField2?.text.toString()}"))
            this.startActivity(intent)
        }

//        searchField?.setText(f)
//        Log.d("qqq", f)
//        searchField2?.setText(s)

        searchFriends?.setOnClickListener {
            doAsync {
                var nameByIdvk = MainActivity.database?.personDao()?.getNameByVkid(searchField?.text.toString().toInt())
                var nameByIdvk2 = MainActivity.database?.personDao()?.getNameByVkid(searchField2?.text.toString().toInt())
                uiThread {
                    baos = ByteArrayOutputStream()
                    var image = view?.findViewById<ImageView>(R.id.iv_icon_first)
            image?.isDrawingCacheEnabled = true
                    val imageWidth = image?.width
                    val imageH = image?.height
            image?.layout(0, 0, 1550 , 1550)//image?.measuredWidth, image?.measuredHeight);
                    image?.buildDrawingCache(true)
                    val bitmap = Bitmap.createBitmap(image?.drawingCache)
            image?.layout(0, 0, imageWidth!! , imageH!!)
            image?.isDrawingCacheEnabled = false


                    baos2 = ByteArrayOutputStream()
                    var image2 = view?.findViewById<ImageView>(R.id.iv_icon_second)
            image2?.isDrawingCacheEnabled = true
                    val imageWidth2 = image2?.width
                    val imageH2 = image2?.height
            image2?.layout(0, 0, 1550 , 1550)
            image2?.buildDrawingCache(true)
                    //val bitmap2 = image2?.getDrawingCache()
                    val bitmap2 = Bitmap.createBitmap(image2?.drawingCache)
            image2?.layout(0, 0, imageWidth2!! , imageH2!!)
            image2?.isDrawingCacheEnabled = false
                    doAsync {
                        var bm = bitmap
                        bm?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        var bm2 = bitmap2
                        bm2?.compress(Bitmap.CompressFormat.JPEG, 100, baos2)
                        uiThread {
                            var imageBytes = baos!!.toByteArray()
                            var imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                            var person = Person(searchField?.text.toString().toInt(), name = textOnIcon1?.text.toString(), photo= imageString!!)
                            var imageBytes2 = baos2!!.toByteArray()
                            var imageString2 = Base64.encodeToString(imageBytes2, Base64.DEFAULT)
                            var person2 = Person(searchField2?.text.toString().toInt(), name = textOnIcon2?.text.toString(), photo= imageString2!!)
                            doAsync {
                                MainActivity.database?.personDao()?.insert(person)
                                MainActivity.database?.personDao()?.insert(person2)
                            }
                        }
                    }
                }
            }

            val intent = Intent(context, Main2ActivityFriends/*(searchField?.text.toString())*/::class.java)
            intent.putExtra("id", searchField?.text.toString())
            intent.putExtra("id2", searchField2?.text.toString())
            startActivity(intent)
        }
//        searchUsers?.setOnClickListener {
//            Log.i("info", activity!!.getSharedPreferences("access_token", MODE_PRIVATE)?.getString("token","wtf"))
//            var generatedURL: URL = NetworkUtils.generateURL(searchField?.getText().toString())
//            VKQueryUsers(result!!).execute(generatedURL)
//        }
        searchGroups?.setOnClickListener {

            doAsync {
                var nameByIdvk = MainActivity.database?.personDao()?.getNameByVkid(searchField?.text.toString().toInt())
                var nameByIdvk2 = MainActivity.database?.personDao()?.getNameByVkid(searchField2?.text.toString().toInt())
                uiThread {
                    baos = ByteArrayOutputStream()
                    var image = view?.findViewById<ImageView>(R.id.iv_icon_first)
                    image?.buildDrawingCache()
                    val bitmap = image?.getDrawingCache()
                    baos2 = ByteArrayOutputStream()
                    var image2 = view?.findViewById<ImageView>(R.id.iv_icon_second)
                    image2?.buildDrawingCache()
                    val bitmap2 = image2?.getDrawingCache()
                    doAsync {
                        var bm = bitmap
                        bm?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        var bm2 = bitmap2
                        bm2?.compress(Bitmap.CompressFormat.JPEG, 100, baos2)
                        uiThread {
                            var imageBytes = baos!!.toByteArray()
                            var imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                            var person = Person(searchField?.text.toString().toInt(), name = textOnIcon1?.text.toString(), photo= imageString!!)
                            var imageBytes2 = baos2!!.toByteArray()
                            var imageString2 = Base64.encodeToString(imageBytes2, Base64.DEFAULT)
                            var person2 = Person(searchField2?.text.toString().toInt(), name = textOnIcon2?.text.toString(), photo= imageString2!!)
                            doAsync {
                                MainActivity.database?.personDao()?.insert(person)
                                MainActivity.database?.personDao()?.insert(person2)
                            }
                        }
                    }
                }
            }


            val intent = Intent(context, Main3ActivityGroups/*(searchField?.text.toString())*/::class.java)
            intent.putExtra("id", searchField?.text.toString())
            intent.putExtra("id2", searchField2?.text.toString())
            startActivity(intent)
        }
        searchField?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchField?.text.toString() != "") {
                    var generatedURL: URL = NetworkUtils.generateURL(searchField?.text.toString())
                    getPhotoURL(icon1, textOnIcon1).execute(generatedURL)
                }
            }
        })
        searchField2?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchField2?.text.toString() != "") {
                    var generatedURL: URL = NetworkUtils.generateURL(searchField2?.text.toString())
                    getPhotoURL(icon2, textOnIcon2).execute(generatedURL)
                }
            }

        })
    }
    inner class getPhotoURL(val icon: ImageView?, val textViewOnIcon: TextView?): AsyncTask<URL, Void, String?>(){
        override fun doInBackground(vararg params: URL?): String? {
            var response: String? = NetworkUtils.getResponseFromURL2(params[0]!!)

            return response.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                var jsonResponse = JSONObject(result)
                //var jsonResponse = JSONObject(response.first)
                //var jsonObject: JSONObject = jsonResponse.getJSONObject("response")
                var jsonArray: JSONArray = jsonResponse.getJSONArray("response")
                val tempJsonObject = jsonArray[0] as JSONObject
                textViewOnIcon?.text =
                    tempJsonObject.optString("first_name", "") + " " + tempJsonObject.optString("last_name", "")
                getPhoto(icon).execute(URL(tempJsonObject.optString("photo_100", "")))
            }
            catch(e: Exception){
                Toast.makeText(view?.context, "Error: id's not exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    inner class getPhoto(val icon: ImageView?): AsyncTask<URL, Void, Bitmap?>(){
        override fun doInBackground(vararg params: URL?): Bitmap? {
            try {
                val url = URL(params[0]?.toString())
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                return image
            } catch (e: IOException) {
                println(e)
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            icon?.setImageBitmap(result)
        }

    }
    class VKQueryUsers(result:TextView): AsyncTask<URL, Void, String>() {
        var textViewResult: TextView = result

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: URL?): String {
            var response: String? = getResponseFromURL(params[0]!!)

            return response.toString()
        }

        override fun onPostExecute(response: String?) {
            var firstName: String? = null
            var lastName: String? = null

            if(response != null && !response.equals("")) {
                super.onPostExecute(response)
                var jsonResponse: JSONObject = JSONObject(response)
                var jsonArray: JSONArray = jsonResponse.getJSONArray("response")
                var resultingString: String = ""
                for(i:Int in 0 until jsonArray.length()){
                    var userInfo: JSONObject = jsonArray.getJSONObject(i)

                    firstName = userInfo.getString("first_name")
                    lastName = userInfo.getString("last_name")
                    resultingString += "Имя: " + firstName + "\n" + "Фамилия: " + lastName + "\n\n"
                }

                textViewResult.text = resultingString

            } else{

            }

        }
    }


//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString("id1", "123")//searchField?.text.toString())
//        outState.putString("id2", "321")//searchField2?.text.toString())
//        Log.d("ddd", "onSaveInstanceState")
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if(savedInstanceState != null)
//            f = savedInstanceState!!.getString("id1")
//        try {
//            Log.d("uuu", savedInstanceState!!.getString("id1"))
//            //et_search_field.setText(savedInstanceState!!.getString("id1"))
//            //et_search_field2.setText(savedInstanceState!!.getString("id2"))
//            f = savedInstanceState!!.getString("id1")
//            s = savedInstanceState!!.getString("id2")
//        }
//        catch (e: java.lang.Exception){
//            Log.d("sss", "onRestoreInstanceState")
//        }
//    }
}