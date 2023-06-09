package com.youthworkhub.ui.activity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.youthworkhub.R
import com.youthworkhub.databinding.ActivityMainBinding
import com.youthworkhub.model.UserModel
import com.youthworkhub.utils.Constants
import com.youthworkhub.utils.PreferencesManager
import com.youthworkhub.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var user: UserModel? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, can post notifications.
            Firebase.messaging.isAutoInitEnabled = true
        } else {
            // Notification permission denied.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        Log.d("LoginTAG", "SHARED data: ${user}")
        requestNotificationPermission()
        setupMainNav()
        setViewModelListeners()

        binding.openSettings.setOnClickListener {
            openAlertDialog()
        }
    }

    private fun setViewModelListeners() {
        mainViewModel.switchFragment.observe(this) {fragment ->
            when (fragment) {
                Constants.HOME_FRAGMENT -> {
                    openFragment(R.id.nav_item_home)
                }
            }
        }
    }

    private fun openAlertDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle(R.string.log_out)
        builder.setMessage(R.string.log_out_messege)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            Firebase.auth.signOut()
            PreferencesManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton(R.string.no) { _, _ -> }
        builder.show()
    }

    private fun setupMainNav() {
        binding.mainBottomNav.selectedItemId = R.id.item_home
        binding.mainBottomNav.setOnItemSelectedListener {
            user = PreferencesManager.getUser()
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(R.id.nav_item_home)
                    binding.screenTitle.text = resources.getString(R.string.jobs_title)
                    return@setOnItemSelectedListener true
                }
                R.id.item_create_job -> {
                    if (user != null) {
                        openFragment(R.id.nav_item_create_job)
                        binding.screenTitle.text = resources.getString(R.string.create_job_title)
                        return@setOnItemSelectedListener true
                    } else {
                        Toast.makeText(applicationContext, R.string.create_account, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.item_saved -> {
                    openFragment(R.id.nav_item_saved)
                    binding.screenTitle.text = resources.getString(R.string.saved_title)
                    return@setOnItemSelectedListener true
                }
                R.id.item_account -> {
                    if (user != null) {
                        openFragment(R.id.nav_item_account)
                        binding.screenTitle.text = resources.getString(R.string.account_title)
                        return@setOnItemSelectedListener true
                    } else {
                        Toast.makeText(applicationContext, R.string.create_account, Toast.LENGTH_SHORT).show()
                    }

                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun openFragment(id: Int) {
        findNavController(R.id.main_fragment_container).navigate(id)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and app) can post notifications.
                Firebase.messaging.isAutoInitEnabled = true
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // Notification permission taken away or previously denied
            } else {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

}