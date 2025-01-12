package com.example.androidchatapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.R
import com.example.androidchatapp.models.User

class ContactsAdapter(
    private val contacts: ArrayList<User> = ArrayList()
): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val profile: ImageView = itemView.findViewById(R.id.profile)
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.single_contact, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact: User = contacts[position]

        holder.name.text = contact.name
        holder.phone.text = contact.phone
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(contacts: ArrayList<User>) {
        this.contacts.clear()
        this.contacts.addAll(contacts)
        notifyDataSetChanged()
    }
}