package com.example.androidchatapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidchatapp.models.FetchMessagesModel
import com.example.androidchatapp.models.Message
import com.example.androidchatapp.models.SendMessageModel
import com.example.androidchatapp.utils.MySharedPreference
import com.example.androidchatapp.utils.Utility
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.Charset

class ChatActivity : AppCompatActivity() {

    lateinit var message: EditText
    lateinit var btnSend: Button
    var phone: String = ""
    lateinit var toolbar: Toolbar
    lateinit var sharedPreference: MySharedPreference
    lateinit var messages: ArrayList<Message>

    lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        messages = ArrayList()
        sharedPreference = MySharedPreference()
        message = findViewById(R.id.message)
        btnSend = findViewById(R.id.btnSend)

        rv = findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)

        if (intent.hasExtra("phone") && intent.hasExtra("name")) {
            phone = intent.getStringExtra("phone").toString()
            phone = URLEncoder.encode(phone, "UTF-8")

            getData()

            title = intent.getStringExtra("name").toString()
            btnSend.setOnClickListener {
                btnSend.isEnabled = false

                val queue = Volley.newRequestQueue(this)
                val url = Utility.apiUrl + "/chats/send"

                val requestBody = "phone=" + phone + "&message=" + message.text
                val stringRequest = object : StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        Log.i("Encrypted", response)

                        btnSend.isEnabled = true
                        val sendMessageModel: SendMessageModel =
                            Gson().fromJson(response, SendMessageModel::class.java)

                        if (sendMessageModel.status == "success") {
                            //
                        } else {
                            Utility.showAlert(this, "Error", sendMessageModel.message)
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.i("Encrypt error", error.toString())
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
        }
    }

    fun getData() {
        val queue = Volley.newRequestQueue(this)
        val url = Utility.apiUrl + "/chats/fetch"
        val requestBody = "phone=" + phone
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                val fetchMessagesModel: FetchMessagesModel =
                    Gson().fromJson(response, FetchMessagesModel::class.java)
                if(fetchMessagesModel.status == "success") {
                    messages = fetchMessagesModel.data
                } else {
                    Utility.showAlert(this, "Error", fetchMessagesModel.message)
                }
            }, Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer " + sharedPreference.getAccessToken(applicationContext)
                return headers
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charset.defaultCharset())
            }
        }
        queue.add(stringRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}