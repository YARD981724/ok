package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Adapter.MessageAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Message
import com.yaduvanshi_ashok_rd.likee.R
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.util.Date


class chatActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userProfile:ImageView
    private lateinit var userName:TextView
    private lateinit var messageBox:EditText
    private lateinit var sentButton:ImageView
    private lateinit var viewOnline:TextView
    private lateinit var attachment:ImageView
    private lateinit var chatRecyclerview:RecyclerView
    private lateinit var leftArrow:ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList:ArrayList<Message>
    private lateinit var mDbRef:DatabaseReference

    var receiverRoom:String? = null
    var senderRoom:String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        window.statusBarColor = Color.BLACK
        userName = findViewById(R.id.user_name)
        userProfile = findViewById(R.id.option_profile)
        chatRecyclerview = findViewById(R.id.charRecyclerview)
        sentButton = findViewById(R.id.sentButton)
        messageBox = findViewById(R.id.messageBox)
        leftArrow = findViewById(R.id.left_arrow)
        viewOnline = findViewById(R.id.view_online)
        attachment = findViewById(R.id.attachment)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val name = intent.getStringExtra("name")
        val profile = intent.getStringExtra("image")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        userName.text = name
        Glide.with(this).load(profile).placeholder(R.drawable.profile).into(userProfile)

//        userProfile.setOnClickListener {
//            val intent = Intent(this, ProfileDetail::class.java)
//                intent.putExtra("name", userName.text)
//                intent.getStringExtra("image")
//                intent.putExtra("uid", firebaseUser.uid)
//                this.startActivity(intent)
//
////
//        }

        leftArrow.setOnClickListener { finish() }

        // online or offline
        mDbRef.child("Presence").child(receiverUid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val status = snapshot.getValue(String::class.java)
                    if (status == "Offline"){
                        viewOnline.visibility = View.GONE
                    }else{
                        viewOnline.setText(status)
                        viewOnline.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
// online offline

        messageList = ArrayList()
       messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerview.layoutManager = LinearLayoutManager(this)
        chatRecyclerview.adapter = messageAdapter
        // logic for adding data to recyclerview
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        // adding the message to database
        sentButton.setOnClickListener {
            val message = messageBox.text.toString()
            val date = Date()

            val messageObject = Message(message, senderUid, date.time)
            if (message.isEmpty()){
                Toast.makeText(this, "message box is empty", Toast.LENGTH_SHORT).show()
            }else{
                val randomKey = mDbRef.push().key
                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message
                lastMsgObj["lastMsg"] = date.time

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")

                // last message
                // last message

            }


        }
        attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 101)

        }

// online or offline
    val Handler = Handler()
        messageBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                mDbRef.child("Presence").child(senderUid!!).setValue("typing...")
                Handler.removeCallbacksAndMessages(null)
                Handler.postDelayed(userStopTyping, 1000)

            }
            var userStopTyping = Runnable {
                mDbRef.child("Presence")
                    .child(senderUid!!)
                    .setValue("Online")

            }

        })
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101){

        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        mDbRef.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
//        val now = LocalDateTime.now()
        val now = LocalDateTime.now()
        val startOfDay = now.toKotlinLocalDateTime()
        val currentId = FirebaseAuth.getInstance().uid
        mDbRef.child("Presence").child(currentId!!)
            .setValue("$startOfDay")
    }
    // online or offline

}