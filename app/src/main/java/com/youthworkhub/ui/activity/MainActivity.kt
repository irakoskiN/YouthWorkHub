package com.youthworkhub.ui.activity

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
    }

    private fun setupMainNav() {
        binding.mainBottomNav.selectedItemId = R.id.item_home
        binding.mainBottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(R.id.nav_item_home)
                    return@setOnItemSelectedListener true
                }
                R.id.item_search_job -> {
                    openFragment(R.id.nav_item_search)
                    return@setOnItemSelectedListener true
                }
                R.id.item_saved -> {
                    openFragment(R.id.nav_item_saved)
                    return@setOnItemSelectedListener true
                }
                R.id.item_account -> {
                    openFragment(R.id.nav_item_account)
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