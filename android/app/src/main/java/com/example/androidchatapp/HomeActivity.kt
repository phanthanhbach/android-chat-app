package com.example.androidchatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidchatapp.adapters.ViewPagerAdapter
import com.example.androidchatapp.fragments.ChatsFragment
import com.example.androidchatapp.fragments.ContactsFragment
import com.example.androidchatapp.models.GeneralResponse
import com.example.androidchatapp.models.GetUserModel
import com.example.androidchatapp.models.User
import com.example.androidchatapp.utils.MySharedPreference
import com.example.androidchatapp.utils.Utility
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.Charset

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var nav_view: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var viewPager: ViewPager

    lateinit var sharedPreference: MySharedPreference
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        sharedPreference = MySharedPreference()
        drawerLayout = findViewById(R.id.drawerLayout)
//        actionBarDrawerToggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//
//        drawerLayout.addDrawerListener(actionBarDrawerToggle)
//        actionBarDrawerToggle.syncState()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        viewPager = findViewById(R.id.view_pager)

        val adapter = ViewPagerAdapter(this, supportFragmentManager)
        adapter.addFragment(ChatsFragment(), "Chats")
        adapter.addFragment(ContactsFragment(), "Contacts")
        viewPager.adapter = adapter

        viewPager.currentItem = 0
        nav_view = findViewById(R.id.nav_view)

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.chats)
        setToolbarTitle("Chats")

        getData()
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun getData() {
        val queue = Volley.newRequestQueue(this)
        val url: String = Utility.apiUrl + "/me"
        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                val getUserModel: GetUserModel = Gson().fromJson(response, GetUserModel::class.java)
                if (getUserModel.status == "success") {
                    user = getUserModel.user
                    val headerView: View = nav_view.getHeaderView(0)

                    val name: TextView = headerView.findViewById(R.id.name)
                    name.text = user.name

                    val phone: TextView = headerView.findViewById(R.id.phone)
                    phone.text = user.phone

                    if (!sharedPreference.hasSaveContacts(this)) {
                        getContactPermission()
                    }
                } else {
                    Utility.showAlert(this, "Error", getUserModel.message)
                }
            }, Response.ErrorListener { error ->

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers: HashMap<String, String> = HashMap()
                    headers["Authorization"] =
                        "Bearer " + sharedPreference.getAccessToken(applicationContext)
                    return headers
                }
            }
        queue.add(stringRequest)
    }

    fun getContactPermission() {
        val permission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                565
            )
        }
    }

    @SuppressLint("Range")
    fun getContacts() {
        val contacts: JSONArray = JSONArray()

        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        while (phones?.moveToNext() == true) {
            val name: String = phones.getString(
                phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
            val phone: String = phones.getString(
                phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            )

            val obj: JSONObject = JSONObject()
            obj.put("name", name)
            obj.put("phone", phone)
            contacts.put(obj)
        }

        phones?.close()

        val queue = Volley.newRequestQueue(this)
        val url = Utility.apiUrl + "/contacts/save"
        val requestBody = "contacts=" + URLEncoder.encode(contacts.toString(), "UTF-8")

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                val generalResponse: GeneralResponse =
                    Gson().fromJson(response, GeneralResponse::class.java)
                Utility.showAlert(this, "Contacts", generalResponse.message)
                sharedPreference.setContactsSave(this)
            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] =
                    "Bearer " + sharedPreference.getAccessToken(applicationContext)
                return headers
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        queue.add(stringRequest)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 565) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            }
        }
    }

    fun doLogout() {
        val queue = Volley.newRequestQueue(this)
        val url: String = Utility.apiUrl + "/logout"

        val stringRequest: StringRequest =
            object : StringRequest(Method.POST, url, Response.Listener { response ->
                val generalResponse: GeneralResponse =
                    Gson().fromJson(response, GeneralResponse::class.java)
                Utility.showAlert(this, "Logout", generalResponse.message, {
                    if (generalResponse.status == "success") {
                        sharedPreference.removeAccessToken(this)
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                })
            }, Response.ErrorListener { error ->
                Utility.showAlert(this, "Logout", "Something went wrong")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers: HashMap<String, String> = HashMap()
                    headers["Authorization"] =
                        "Bearer " + sharedPreference.getAccessToken(applicationContext)
                    return headers
                }
            }
        queue.add(stringRequest)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.chats -> {
                viewPager.currentItem = 0
                setToolbarTitle("Chats")
            }

            R.id.contacts -> {
                viewPager.currentItem = 1
                setToolbarTitle("Contacts")
            }

            R.id.logout -> {
                Utility.showAlert(this, "Logout", "Are you sure you want to logout?", {
                    doLogout()
                })
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}