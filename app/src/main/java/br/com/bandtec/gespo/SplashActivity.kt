package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    val valor = 1;

    var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash)
        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)
        check();
    }

    private fun check(){
        Handler().postDelayed({
            val offlineData = preferences?.getInt("id", 0)
            if(offlineData === 0){
                val loginActivity = Intent(this, LoginActivity::class.java)
                startActivity(loginActivity)
            }else{
                val mainActivity = Intent(this, MainActivity::class.java)
                startActivity(mainActivity)
            }
        }, 3000)
    }
}