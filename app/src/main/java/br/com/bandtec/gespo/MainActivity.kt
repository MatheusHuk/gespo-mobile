package br.com.bandtec.gespo

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.bandtec.gespo.utils.changeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_dashboard

        navView.setOnNavigationItemSelectedListener(
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            changeActivity(item ,this.applicationContext)
            return@OnNavigationItemSelectedListener true
        })
    }
}