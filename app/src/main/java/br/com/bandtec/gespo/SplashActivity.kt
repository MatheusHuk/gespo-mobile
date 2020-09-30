package br.com.bandtec.gespo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    val valor = 1;

    override fun onCreate(savedInstanceState: Bundle?){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)
        //check();
    }

    private fun check(){
        if(this.valor === 1){
            setContentView(R.layout.activity_login)
        }
    }
}