package com.jay.firebaseminipracticeproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.jay.firebaseminipracticeproject.onBoarding.GettingStarted

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowInsets.Type.statusBars(),
            WindowInsets.Type.statusBars()
        )


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, GettingStarted::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
