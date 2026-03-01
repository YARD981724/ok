package com.yaduvanshi_ashok_rd.likee.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.Model.Message
import com.yaduvanshi_ashok_rd.likee.R
import com.yaduvanshi_ashok_rd.likee.databinding.DeleteForReceiveBinding
import com.yaduvanshi_ashok_rd.likee.databinding.DeleteLayoutBinding

class MessageAdapter(
    val context: Context,
    var messageList: ArrayList<Message>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 1
    val ITEM_RECEVIE = 2
    var senderRoom:String?=null
    var receiverRoom:String?=null




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // inflate receive
            val view = LayoutInflater.from(context).inflate(R.layout.reciever_layout, parent, false)
            return ReceiveViewHolder(view)
        } else {
            // inflate sent
            val view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false)
            return SentViewHolder(view)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            //do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder

            if (currentMessage.message.equals("photo")){
                holder.sentImage.visibility = View.VISIBLE
                holder.sentMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.loading).into(holder.sentImage)
            }
            holder.sentMessage.text = currentMessage.message
            holder.receiveTIme.text = java.sql.Timestamp(currentMessage.timestamp).toString()
            holder.receiveTIme.visibility = View.VISIBLE
            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_for_receive, null)
                val binding: DeleteForReceiveBinding = DeleteForReceiveBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.DeleteForReceive.setOnClickListener {
                    currentMessage.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child("messages")
//                            .child(senderRoom!!)
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()

                }
                binding.cancelReceive.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }


        } else {
            val viewHolder = holder as ReceiveViewHolder
            if (currentMessage.message.equals("photo")){
                holder.receiveImage.visibility = View.VISIBLE
                holder.receiveMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.loading).into(holder.receiveImage)
            }
            holder.receiveMessage.text = currentMessage.message
            holder.sentTime.text = java.sql.Timestamp(currentMessage.timestamp).toString()
            holder.sentTime.visibility = View.VISIBLE

            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)

                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.everyone.setOnClickListener {
                    currentMessage.messageId = "This message was deleted"
                    currentMessage.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
//                            .child(senderRoom!!)

                            .child("messages")
                            .child(it1).setValue(currentMessage)

                    }

                    currentMessage.messageId.let { it1->
                        FirebaseDatabase.getInstance().reference.child("chats")
//                            .child(receiverRoom!!)

                            .child("messages")
                            .child(it1!!).setValue(currentMessage)
                    }
                    dialog.dismiss()
                }
                binding.forMe.setOnClickListener {
                    currentMessage.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
//                            .child(senderRoom!!)
                            .child("messages")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()

                }
                binding.cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }




        }


    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser!!.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEVIE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)
        val receiveTIme: TextView = itemView.findViewById(R.id.receiveTime)
        val sentImage:ImageView = itemView.findViewById(R.id.receive_image)


    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.receive_message)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        val receiveImage:ImageView = itemView.findViewById(R.id.sent_image)
    }


}