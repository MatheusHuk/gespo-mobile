package br.com.bandtec.gespo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CounterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter)
        check();

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_counter

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }
}
