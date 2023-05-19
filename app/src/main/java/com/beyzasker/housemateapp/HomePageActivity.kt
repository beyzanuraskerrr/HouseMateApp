package com.beyzasker.housemateapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        loadFragment(HomeFragment())
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.announcement -> {
                    loadFragment(AnnouncementFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profileOption -> {
                val redirectToProfileIntent =
                    Intent(this@HomePageActivity, ProfileActivity::class.java)
                startActivity(redirectToProfileIntent)
                true
            }
            R.id.logoutOption -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        auth.signOut()

        if (auth.currentUser == null) {
            val redirectToLoginIntent = Intent(this@HomePageActivity, LoginActivity::class.java)
            startActivity(redirectToLoginIntent)
        }
    }
}