package com.yaduvanshi_ashok_rd.likee.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.yaduvanshi_ashok_rd.likee.R


class AddFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        getAction()
        return view
    }
    private fun getAction(){
        val intent = Intent()
        intent.action
        intent.type = "image/*"
    }


}


