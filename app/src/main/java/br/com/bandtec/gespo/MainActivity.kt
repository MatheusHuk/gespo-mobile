package br.com.bandtec.gespo

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when(item.itemId){
                    R.id.navigation_timer -> {
                        val timerView = Intent(this, SplashActivity::class.java)
                        startActivity(timerView)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_apontamentos -> {
                        val apontamentosView = Intent(this, LoginActivity::class.java)
                        startActivity(apontamentosView)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile -> {
                        val profileView = Intent(this, LoginActivity::class.java)
                        startActivity(profileView)
                        return@OnNavigationItemSelectedListener true
                    }
                    else -> {
                        return@OnNavigationItemSelectedListener false
                    }
                }
            })
    }
}