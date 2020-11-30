package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.activity_timer.cl_tela_inteira
import kotlinx.android.synthetic.main.activity_timer.loading
import kotlinx.android.synthetic.main.activity_timesheet_consult.*

class TimerActivity : AppCompatActivity(){

    var preferences: SharedPreferences? = null

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        //esse é o código do spinner
        //ele ta puxando lá do arquivo strings.xml as opções
        sp_options.adapter = ArrayAdapter(this,
        R.layout.support_simple_spinner_dropdown_item,
        resources.getStringArray(R.array.options))

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }

    fun logOff(v:View){
        loading.visibility = View.VISIBLE
        cl_tela_inteira.visibility = View.GONE

        val editor = preferences?.edit()

        editor?.remove("id")
        editor?.remove("username")
        editor?.remove("cookie")
        editor?.commit()

        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
    }

    fun abrirFiltro(v: View){

    }
}