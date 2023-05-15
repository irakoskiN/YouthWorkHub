package com.youthworkhub.ui.activity

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.youthworkhub.databinding.ActivityLoginBinding
import com.youthworkhub.model.UserModel
import com.youthworkhub.utils.Helpers
import com.youthworkhub.utils.PreferencesManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2
    override fun onResume() {
        super.onResume()
        initAuth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonClicks()
        setupGoogleClick()
        googleAuthInit()

    }

    fun googleAuthInit(){
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("891149307635-35bt3ff3n5bicl4oj9schs3hrs4mb7jb.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("891149307635-35bt3ff3n5bicl4oj9schs3hrs4mb7jb.apps.googleusercontent.com")
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("GOOGLE", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.d("GOOGLE", e.localizedMessage)
            }
    }
    fun setupGoogleClick(){
        binding.imageViewGoogle.setOnClickListener {
            singUpWithGoogle()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("GOOGLE", "onActivityResult")
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null ->  {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("GOOGLE", "signInWithCredential:success")
                                        val user = auth.currentUser
                                        if (user != null) {
                                            val userData = UserModel(
                                                user.uid,
                                                user.email.toString(),
                                                user.displayName.toString(),
                                            )
                                            Firebase.firestore.collection("users")
                                                .document(auth.currentUser!!.uid)
                                                .set(userData)
                                                .addOnCompleteListener(this) {
                                                    if (it.isSuccessful) {
                                                        Log.i("RegisterTag", "success saving user")
                                                        PreferencesManager.putUser(userData)
                                                        Log.i(
                                                            "RegisterTag",
                                                            "success saving user ${PreferencesManager.getUser()}"
                                                        )
                                                        val intent =
                                                            Intent(this, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Log.i("RegisterTag", "error saving user")
                                                    }
                                                }
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(
                                                "GOOGLE",
                                                "signInWithCredential:failure",
                                                task.exception
                                            )
                                        }
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("GOOGLE", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }
        }
    }

    private fun initAuth() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.i("AUTH", "currentUser ${currentUser}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupButtonClicks() {
        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                login(email, pass)
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("LoginTAG", "login successful")
                    Firebase.firestore.collection("users").document(task.result.user!!.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("LoginTAG", "DocumentSnapshot data: ${document.data}")
                                val userData = Helpers.parseFbUserToUserModal(document.data)
                                if (userData != null) {
                                    PreferencesManager.putUser(userData)
                                }

                            } else {
                                Log.d("LoginTAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("LoginTAG", "get failed with ", exception)
                        }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.i("LoginTAG", "login failed", task.exception)
                    // todo show dialog
                }
            }
    }

    private fun singUpWithGoogle(){
        Log.e("GOOGLE", "clcked ${signUpRequest}")
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("GOOGLE", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                Log.d("GOOGLE", e.localizedMessage)
            }
    }
}