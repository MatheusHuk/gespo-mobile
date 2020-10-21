package br.com.bandtec.gespo

import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.marginTop
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timesheet_consult.*

class TimesheetConsultActivity : AppCompatActivity() {

    var estadoFiltro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_consult)

            sp_periodo.adapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.periodos))


            val s= mutableListOf<String>()

            s.add("projeto 1")
            s.add("projeto 2")
            s.add("projeto 3")

            sp_projeto.adapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,
            s)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_apontamentos

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }

    fun abrirFiltro(componente:View){
        val params: ViewGroup.MarginLayoutParams = tl_tabela_consulta.layoutParams as ViewGroup.MarginLayoutParams

        if (estadoFiltro) {
            //tornando os formulários do filtro de busca invisíveis
            v_fundo_form_top.visibility = View.GONE
            v_fundo_form_bottom.visibility = View.GONE

            //tornando a linha visível para se adequar a estilização do app
            iv_gespo_line.visibility = View.VISIBLE

            //ajustando o table layout na tela para se adequar as mudanças
            //criando uma variável do tipo Layout Params
            params.topMargin = TypedValue.COMPLEX_UNIT_DIP *290
            tl_tabela_consulta.layoutParams = params

            tl_tabela_consulta.invalidate()
            tl_tabela_consulta.requestLayout()

            tv_txt_detalhes.visibility = View.VISIBLE

            estadoFiltro = false
        }
        else{
        //ajustando o table layout na tela para se adequar as mudanças
        //criando uma variável do tipo Layout Params
        params.topMargin = TypedValue.COMPLEX_UNIT_DIP * 510
        tl_tabela_consulta.layoutParams = params

        tl_tabela_consulta.invalidate()
        tl_tabela_consulta.requestLayout()

        //tornando os formulários do filtro de busca visíveis
        v_fundo_form_top.visibility = View.VISIBLE
        v_fundo_form_bottom.visibility = View.VISIBLE

        //tornando a linha invisível para se adequar a estilização do app
        iv_gespo_line.visibility = View.GONE

            tv_txt_detalhes.visibility = View.GONE
            estadoFiltro = true

        }
    }

}