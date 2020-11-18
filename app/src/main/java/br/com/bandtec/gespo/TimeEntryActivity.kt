package br.com.bandtec.gespo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timesheet_consult.*

class TimeEntryActivity : AppCompatActivity() {

    var estadoFiltro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_entry)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_apontamentos

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }
    fun abrirFiltro(componente: View){
        if(estadoFiltro){
            v_fundo_form_top.visibility = View.GONE
            v_fundo_form_bottom.visibility = View.GONE

            estadoFiltro = false
        }else{
            v_fundo_form_top.visibility = View.VISIBLE
            v_fundo_form_bottom.visibility = View.VISIBLE

            estadoFiltro = true
        }
    }
}