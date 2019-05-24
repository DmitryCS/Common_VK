package com.example.vk_sdk_v3

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.vk_sdk_v3.NetworkUtils.Companion.generateURL
import com.example.vk_sdk_v3.NetworkUtils.Companion.generateURL2
import com.example.vk_sdk_v3.NetworkUtils.Companion.getResponseFromURL
import com.example.vk_sdk_v3.NetworkUtils.Companion.getResponseFromURL2
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import com.vk.api.sdk.requests.VKRequest
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var sharedPreference: SharedPreferences?= null

    companion object {
        var database: MyDatabase2? = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, HomeFragment()).commit()
            }
            R.id.nav_gallery -> {
                supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, EmptyFragment()).commit()
            }
            R.id.nav_slideshow -> {
                supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, EmptyFragment()).commit()
            }
            R.id.nav_tools -> {
                supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, EmptyFragment()).commit()
            }
            R.id.nav_share -> {
                Toast.makeText(this, "Sorry, not ready.",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                Toast.makeText(this, "sasdfsdf",Toast.LENGTH_SHORT).show()
            }
            R.id.nav_history ->{
                supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, HistoryFragment()).addToBackStack(null).commit()
            }
            R.id.nav_delData ->{
                doAsync {
                    database!!.personDao().deleteAll()
                    database!!.commonDao().deleteAll()
                    uiThread {
                        supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, HistoryFragment()).addToBackStack(null).commit()
                    }
                }
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreference = this.getSharedPreferences("access_token", MODE_PRIVATE)
        if(sharedPreference?.getString("token", null) == null ) {
            VK.initialize(applicationContext)
            VK.login(this, arrayListOf(VKScope.PHOTOS, VKScope.FRIENDS, VKScope.OFFLINE))
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar_)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        supportFragmentManager.beginTransaction().replace(R.id.conteinerForAll, HomeFragment()).commit()
        toggle.syncState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val tempInt = findViewById<EditText>(R.id.et_search_field)?.text.toString().toInt()
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                val edit = sharedPreference?.edit()
                edit?.putString("token", token.accessToken)
                edit?.commit()
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
