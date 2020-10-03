package br.com.bandtec.gespo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    val valor = 1;

    override fun onCreate(savedInstanceState: Bundle?){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash)
        check();
    }

    private fun check(){
        if(this.valor === 1){
            val main = Intent(this, MainActivity::class.java)
            startActivity(main)
        }
    }
}