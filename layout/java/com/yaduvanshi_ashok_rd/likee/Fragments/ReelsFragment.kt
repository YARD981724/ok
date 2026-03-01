package com.yaduvanshi_ashok_rd.likeee.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yaduvanshi_ashok_rd.likee.Adapter.ReelAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.R


class ReelsFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ReelAdapter
    private lateinit var reelList: MutableList<Reel>
    private lateinit var auth: FirebaseAuth
    private  var firebaseUser: FirebaseUser?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_reels, container, false)
        viewPager = view.findViewById(R.id.reel_viewpager)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        auth = Firebase.auth
        reelList = ArrayList()
        adapter = ReelAdapter(requireContext(), reelList)
        viewPager.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("Reels").get().addOnSuccessListener {
            val tempList = ArrayList<Reel>()
            reelList.clear()
            for (i in it.children){
                val reel: Reel? = i.getValue(Reel::class.java)
                tempList.add(reel!!)
            }
            reelList.addAll(tempList)
            reelList.reverse()
            adapter.notifyDataSetChanged()

        }


        return  view
    }

}