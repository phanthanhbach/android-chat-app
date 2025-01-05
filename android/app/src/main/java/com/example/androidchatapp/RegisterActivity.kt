package com.example.androidchatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidchatapp.models.GeneralResponse
import com.example.androidchatapp.utils.Utility
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.Charset

class RegisterActivity : AppCompatActivity() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var password: EditText
    lateinit var RegisterBtn: Button
    lateinit var Login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
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

        title = "Register"

        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        RegisterBtn = findViewById(R.id.signup_btn)
        Login = findViewById(R.id.login)

        Login.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        RegisterBtn.setOnClickListener {
            val queue = Volley.newRequestQueue(applicationContext)
            val url: String = Utility.apiUrl + "/register"

            val requestBody: String = "name=${name.text}&phone=${
                URLEncoder.encode(
                    phone.text.toString(),
                    "utf-8"
                )
            }&password=${password.text}"

            val stringRequest: StringRequest =
                object : StringRequest(Method.POST, url, Response.Listener { response ->
                    val generalRespons: GeneralResponse = Gson().fromJson(response, GeneralResponse::class.java)
                    Utility.showAlert(this, "Register", generalRespons.message)
                }, Response.ErrorListener { error ->
                    Log.i("error", error.toString())
                }) {
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
            queue.add(stringRequest)
        }
    }
}

