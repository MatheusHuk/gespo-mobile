package br.com.bandtec.gespo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun goToMain(v: View){
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
    }
}