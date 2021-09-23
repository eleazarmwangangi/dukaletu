package com.example.mamamboga.activity

import android.content.Context
import com.example.mamamboga.R
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // shared preferences
        val sharedPref: SharedPreferences = getSharedPreferences("user-welcome", Context.MODE_PRIVATE)

        object : CountDownTimer(3500, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                // if entry not present of is false, go to login, else go to home.
                if ( sharedPref.getBoolean("user-welcome", false)){
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java) //HomeActivity::class.java //LoginActivity::class.java
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java) //HomeActivity::class.java //LoginActivity::class.java
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }
}