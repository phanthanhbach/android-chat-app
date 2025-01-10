package com.example.androidchatapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidchatapp.R
import com.example.androidchatapp.utils.MySharedPreference
import com.example.androidchatapp.utils.Utility

class ChatsFragment : Fragment() {

    lateinit var sharedPreference: MySharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        sharedPreference = MySharedPreference()
        getData()
    }

    fun getData() {
        val queue = Volley.newRequestQueue(context)
        val url: String = Utility.apiUrl + "/contacts/fetch"

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                Log.i("response", response)
            },
            Response.ErrorListener { error -> }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] =
                    "Bearer " + context?.let {
                        sharedPreference.getAccessToken(it)
                    }

                return headers
            }
        }
        queue.add(stringRequest)
    }
}