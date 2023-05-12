package com.youthworkhub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.youthworkhub.R
import com.youthworkhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMainNav()

        binding.openSettings.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupMainNav() {
        binding.mainBottomNav.selectedItemId = R.id.item_home
        binding.mainBottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(R.id.nav_item_home)
                    binding.screenTitle.text = resources.getString(R.string.jobs_title)
                    return@setOnItemSelectedListener true
                }
                R.id.item_create_job -> {
                    openFragment(R.id.nav_item_create_job)
                    binding.screenTitle.text = resources.getString(R.string.create_job_title)
                    return@setOnItemSelectedListener true
                }
                R.id.item_saved -> {
                    openFragment(R.id.nav_item_saved)
                    binding.screenTitle.text = resources.getString(R.string.saved_title)
                    return@setOnItemSelectedListener true
                }
                R.id.item_account -> {
                    openFragment(R.id.nav_item_account)
                    binding.screenTitle.text = resources.getString(R.string.account_title)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun openFragment(id: Int) {
        findNavController(R.id.main_fragment_container).navigate(id)
    }
}