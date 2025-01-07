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
import com.example.androidchatapp.models.LoginModel
import com.example.androidchatapp.utils.MySharedPreference
import com.example.androidchatapp.utils.Utility
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.Charset

class LoginActivity : AppCompatActivity() {
    lateinit var phone: EditText
    lateinit var password: EditText
    lateinit var LoginBtn: Button
    lateinit var Register: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
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

        title = "Login"

        phone = findViewById(R.id.phone)
        password = findViewById(R.id.password)
        LoginBtn = findViewById(R.id.signin_btn)
        LoginBtn.setOnClickListener {
            val queue = Volley.newRequestQueue(applicationContext)
            val url: String = Utility.apiUrl + "/login"
            val requestBody: String = "phone=${
                URLEncoder.encode(
                    phone.text.toString(),
                    "utf-8"
                )
            }&password=${password.text}"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    Log.i("response", response)
                    val loginModel: LoginModel = Gson().fromJson(response, LoginModel::class.java)
                    if (loginModel.status == "success") {
                        val preference: MySharedPreference = MySharedPreference()
                        preference.setAccessToken(applicationContext, loginModel.accessToken)

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Utility.showAlert(this, "Error", loginModel.message)
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("error", error.toString())
                }) {
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
            queue.add(stringRequest)
        }

        Register = findViewById(R.id.register)
        Register.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }


    }
}