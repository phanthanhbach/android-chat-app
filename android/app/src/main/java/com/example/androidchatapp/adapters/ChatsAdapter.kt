package com.example.androidchatapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidchatapp.FullScreenImageActivity
import com.example.androidchatapp.R
import com.example.androidchatapp.models.Message
import com.example.androidchatapp.utils.FetchImageFromInternet

class ChatsAdapter(
    private val messages: ArrayList<Message> = ArrayList(),
    private val phone: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val sender: TextView = itemView.findViewById(R.id.sender)
//        val message: TextView = itemView.findViewById(R.id.message)
//    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.sender)
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val attachmentImageView: ImageView = itemView.findViewById(R.id.attachment)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.sender)
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val attachmentImageView: ImageView = itemView.findViewById(R.id.attachment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.single_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender.phone == phone) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun getData(): ArrayList<Message> {
        return this.messages
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = messages[position]

        if (holder is SentMessageViewHolder) {
            holder.senderTextView.text = message.sender.name
            holder.messageTextView.text = message.message
            if (message.attachment != null) {
                holder.attachmentImageView.visibility = View.VISIBLE

                FetchImageFromInternet(holder.attachmentImageView).execute(message.attachment)

                holder.attachmentImageView.setOnClickListener {
                    val context: Context = holder.itemView.context
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra("image_url", message.attachment)
                    context.startActivity(intent)
                }
            } else {
                holder.attachmentImageView.visibility = View.GONE
            }
        } else if (holder is ReceivedMessageViewHolder) {
            holder.senderTextView.text = message.sender.name
            holder.messageTextView.text = message.message
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(messages: ArrayList<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    fun appendData(message: Message) {
        this.messages.add(message)
        notifyItemInserted(this.messages.size)
    }
}