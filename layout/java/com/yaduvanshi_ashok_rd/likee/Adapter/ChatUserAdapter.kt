package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.Activity.chatActivity
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R

class ChatUserAdapter(
    private val context: Context,
    private val user: List<User>): RecyclerView.Adapter<ChatUserAdapter.viewHolder>() {

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userProfile:ImageView = itemView.findViewById(R.id.userProfile)
        val userName:TextView = itemView.findViewById(R.id.userName)
        val fullName:TextView = itemView.findViewById(R.id.fullName)
        val chatUserItem:ConstraintLayout = itemView.findViewById(R.id.chat_user_item)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_user_layout, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentUser = user[position]
        Glide.with(context).load(currentUser.getImage()).placeholder(R.drawable.profile).into(holder.userProfile)
        holder.userName.text = currentUser.getUsername()
        holder.fullName.text = currentUser.getFullname()



        holder.chatUserItem.setOnClickListener {
            val intent = Intent(context, chatActivity::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("image", currentUser.getImage())
            intent.putExtra("uid", currentUser.getUid())
            context.startActivity(intent)
            
            }
        holder.userProfile.setOnClickListener {
            val intent = Intent(context, ProfileDetail::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("image", currentUser.getImage())
            intent.putExtra("username", currentUser.getFullname())
            intent.putExtra("uid", currentUser.getUid())
            context.startActivity(intent)
        }


//
    }

}