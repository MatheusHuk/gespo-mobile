package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    val valor = 2;

    override fun onCreate(savedInstanceState: Bundle?){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash)
        check();
    }

    private fun check(){
        if(this.valor === 1){
            Handler().postDelayed({
                val loginActivity = Intent(this, LoginActivity::class.java)
                startActivity(loginActivity)
            }, 3000)
        }else{
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.putExtra("username", "TESTE")
            mainActivity.putExtra("id", 4)
            startActivity(mainActivity)
        }
    }
}