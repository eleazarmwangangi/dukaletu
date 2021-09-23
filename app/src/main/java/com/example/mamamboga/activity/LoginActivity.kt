package com.example.mamamboga.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.mamamboga.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var tvCreateAccount: TextView? = null
    private var myProgressBar: ProgressDialog? = null
    //Firebase references
    private var myAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initialise()
    }
    private fun initialise() {
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        tvCreateAccount = findViewById<View>(R.id.tv_register) as TextView
        myProgressBar = ProgressDialog(this)
        myAuth = FirebaseAuth.getInstance()
        // Switch to register screen
        tvCreateAccount!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnLogin!!.setOnClickListener {
            println(":::login button clicked:::")
            // val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // startActivity(intent)
            loginUser()
        }
    }
    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            // Show logging in modal.
            myProgressBar!!.setMessage("Logging in ...")
            myProgressBar!!.show()

            Log.d(TAG, "Logging in user.")

            myAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->

                    myProgressBar!!.hide()

                    if (task.isSuccessful) {
                        // Update shared prefs to indicate user is logged in
                        val sharedPref: SharedPreferences = getSharedPreferences("user-welcome", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.apply {
                            putBoolean("user-welcome", true)
                        }.apply()
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUI() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun goToRegister(){
        val intentRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
        intentRegister.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentRegister)
    }
}