package com.example.androidchatapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androidchatapp.ChatActivity
import com.example.androidchatapp.R
import com.example.androidchatapp.adapters.ContactsAdapter
import com.example.androidchatapp.interfaces.RVInterface
import com.example.androidchatapp.models.FetchContactsModel
import com.example.androidchatapp.models.User
import com.example.androidchatapp.utils.MySharedPreference
import com.example.androidchatapp.utils.Utility
import com.google.gson.Gson

class ChatsFragment : Fragment(), RVInterface {

    lateinit var sharedPreference: MySharedPreference
    lateinit var contacts: ArrayList<User>
    lateinit var rv: RecyclerView
    lateinit var adapter: ContactsAdapter
    lateinit var search_text: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)

        adapter = ContactsAdapter(ArrayList(), this)
        rv.adapter = adapter

        search_text = view.findViewById(R.id.search_text)
        search_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val tempChats: ArrayList<User> = ArrayList()

                for (chat in contacts) {
                    if (chat.name.lowercase().contains(search_text.text, true)) {
                        tempChats.add(chat)
                    }
                }
                adapter.setData(tempChats)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()

        contacts = ArrayList()
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
                val fetchContactsModel: FetchContactsModel =
                    Gson().fromJson(response, FetchContactsModel::class.java)
                if (fetchContactsModel.status == "success") {
                    contacts = fetchContactsModel.contacts
                    adapter.setData(contacts)
                } else {
                    context?.let {
                        Utility.showAlert(it, "Error", fetchContactsModel.message)
                    }
                }
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

    override fun onClick(view: View) {
        val position: Int = rv.getChildAdapterPosition(view)
        val contacts: ArrayList<User> = adapter.getData()
        if (contacts.size > position) {
            val user: User = contacts[position]
            val intent: Intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("phone", user.phone)
            startActivity(intent)
        }
    }
}