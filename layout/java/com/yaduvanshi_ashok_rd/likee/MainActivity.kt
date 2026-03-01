package com.yaduvanshi_ashok_rd.likee

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.yaduvanshi_ashok_rd.likee.Activity.ReelActivity
import com.yaduvanshi_ashok_rd.likee.Activity.SearchActivity
import com.yaduvanshi_ashok_rd.likee.Activity.postActivity
import com.yaduvanshi_ashok_rd.likee.Fragments.HomeFragments
import com.yaduvanshi_ashok_rd.likee.Fragments.ProfileFragment
import com.yaduvanshi_ashok_rd.likeee.fragments.ReelsFragment

class MainActivity : AppCompatActivity() {

    internal var selectedFragment: Fragment? = null
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    moveToFragment(HomeFragments())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.search -> {
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
//                moveToFragment(searchFragmetns())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.add -> {

//                    moveToFragment(AddFragment())

                    val alertDialog = AlertDialog.Builder(this).create()
                    alertDialog.window?.setBackgroundDrawableResource(R.color.colorBlack)
                    alertDialog.setTitle("Post your photos and reels")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Photos") { dialog, which ->
                        val intent = Intent(this, postActivity::class.java)
                        intent.putExtra("userId", FirebaseAuth.getInstance().currentUser!!.uid)
                        this.startActivity(intent)
                        dialog.dismiss()
                    }
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Reels")
                    { dialog, which ->
                        val intent = Intent(this, ReelActivity::class.java)
                        intent.putExtra("userId", FirebaseAuth.getInstance().currentUser!!.uid)
                        this.startActivity(intent)
                        dialog.dismiss()
                    }
                    alertDialog.show()
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, 101)





                    return@OnNavigationItemSelectedListener true


                }


                R.id.reel -> {
                    moveToFragment(ReelsFragment())
//                    startActivity(Intent(this@MainActivity,NotificationActivity::class.java))
                    return@OnNavigationItemSelectedListener true

                }

                R.id.profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true

                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.BLACK
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val publisher = intent.getStringExtra("PUBLISHER_ID")
        if (publisher != null) {
            val prefs: SharedPreferences.Editor? =
                getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                    .edit().apply { putString("profileId", publisher); apply() }

            moveToFragment(ProfileFragment())
        } else
        //to call fragments
            moveToFragment(HomeFragments())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

}






