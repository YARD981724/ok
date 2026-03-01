package com.yaduvanshi_ashok_rd.likee.Activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Adapter.ChatUserAdapter
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R

class ChatUserActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: ChatUserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var search: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var chatShimmer:ShimmerFrameLayout
    private lateinit var dataView:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user)
        window.statusBarColor = Color.BLACK

        chatShimmer = findViewById(R.id.chat_shimmer)
        dataView = findViewById(R.id.data_view)
        auth = Firebase.auth
        userList = ArrayList()
        userAdapter = ChatUserAdapter(this, userList)
        recyclerView = findViewById(R.id.chat_rv_user)
        search = findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        FirebaseDatabase.getInstance().getReference().child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (dataSnapshot in snapshot.children){
                        val currentUser = dataSnapshot.getValue(User::class.java)
                        if (auth.currentUser?.uid != currentUser?.getUid()){
                            userList.add(currentUser!!)
                            chatShimmer.stopShimmer()
                            chatShimmer.visibility = View.GONE
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                if (search.text.toString() == ""){

                }else{
                    recyclerView.visibility = View.VISIBLE
                    searchUser(s.toString().toLowerCase())
                }
            }

        })


    }
    private fun searchUser(input:String) {

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("username")
            .startAt(input)
            .endAt(input + "\uf8ff").addValueEventListener(object:ValueEventListener
            {
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    userList.clear()

                    for(snapshot in datasnapshot.children)
                    {
                        //searching all users
                        val user=snapshot.getValue(User::class.java)
                        if(user!=null)
                        {
                            userList.add(user)
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }
            })
    }
}