package com.youthworkhub.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.youthworkhub.R
import com.youthworkhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMainNav()
        binding.createJob.setOnClickListener {
            val intent = Intent(this, CreateJobActivity::class.java)
            startActivity(intent)
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
                R.id.item_search_job -> {
                    openFragment(R.id.nav_item_search)
                    binding.screenTitle.text = resources.getString(R.string.search_title)
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