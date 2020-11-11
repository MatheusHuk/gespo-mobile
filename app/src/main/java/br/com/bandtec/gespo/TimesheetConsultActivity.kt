package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.marginTop
import br.com.bandtec.gespo.model.Project
import br.com.bandtec.gespo.model.dashboards.ManagerDashOne
import br.com.bandtec.gespo.requests.ProjectRequest
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timesheet_consult.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TimesheetConsultActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    val projects = mutableListOf<String>()

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    var estadoFiltro = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_consult)

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

            sp_periodo.adapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.periodos))

            val api = Retrofit.Builder()
            .baseUrl("https://gespo-rest.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

            val projectRequest = api.create(ProjectRequest::class.java)

            val getProject = projectRequest.getProjectsByEmployee(cookie,id)



            getProject.enqueue(object:Callback<List<Project>> {
                override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(
                    call: Call<List<Project>>,
                    response: Response<List<Project>>
                ) {
                    response.body()?.forEach{project ->
                        projects.add(project.name)

                    }
                    sp_projeto.adapter = ArrayAdapter(applicationContext,
                        R.layout.support_simple_spinner_dropdown_item,
                        projects)
                }
            })

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_apontamentos

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }

    fun abrirFiltro(componente:View){
        val params: ViewGroup.MarginLayoutParams = sv_scroll.layoutParams as ViewGroup.MarginLayoutParams

        if (estadoFiltro) {
            //tornando os formulários do filtro de busca invisíveis
            v_fundo_form_top.visibility = View.GONE
            v_fundo_form_bottom.visibility = View.GONE

            //tornando a linha visível para se adequar a estilização do app
            iv_gespo_line.visibility = View.VISIBLE

            //ajustando o table layout na tela para se adequar as mudanças
            //criando uma variável do tipo Layout Params
            params.topMargin = TypedValue.COMPLEX_UNIT_DIP *280
            sv_scroll.layoutParams = params

            sv_scroll.invalidate()
            sv_scroll.requestLayout()

            tv_txt_detalhes.visibility = View.VISIBLE

            estadoFiltro = false
        }
        else{
        //ajustando o table layout na tela para se adequar as mudanças
        //criando uma variável do tipo Layout Params
        params.topMargin = TypedValue.COMPLEX_UNIT_DIP * 480
        sv_scroll.layoutParams = params

        sv_scroll.invalidate()
        sv_scroll.requestLayout()

        //tornando os formulários do filtro de busca visíveis
        v_fundo_form_top.visibility = View.VISIBLE
        v_fundo_form_bottom.visibility = View.VISIBLE

        //tornando a linha invisível para se adequar a estilização do app
        iv_gespo_line.visibility = View.GONE

            tv_txt_detalhes.visibility = View.GONE
            estadoFiltro = true

        }
    }

    fun goToTimeEntry(v: View){
        val timeEntryActivity = Intent(this, TimeEntryActivity::class.java)
        startActivity(timeEntryActivity)
    }

}