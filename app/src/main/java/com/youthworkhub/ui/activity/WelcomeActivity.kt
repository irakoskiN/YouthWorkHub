package com.youthworkhub.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.youthworkhub.adapter.WelcomeSectionAdapter
import com.youthworkhub.databinding.ActivityWelcomeBinding
import com.youthworkhub.ui.fragments.WelcomeFirstFragment
import com.youthworkhub.ui.fragments.WelcomeSecondFragment
import com.youthworkhub.ui.fragments.WelcomeThirdFragment

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
    }

    private fun setupAdapter() {
        val fragmentList: MutableList<Fragment> = mutableListOf()
        fragmentList.add(WelcomeFirstFragment())
        fragmentList.add(WelcomeSecondFragment())
        fragmentList.add(WelcomeThirdFragment())

        val welcomeSectionAdapter = WelcomeSectionAdapter(
            supportFragmentManager,
            lifecycle,
            fragmentList
        )

        binding.welcomeViewPager.adapter = welcomeSectionAdapter
        binding.welcomeViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

}