package br.com.bandtec.gespo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.model.CostCenter
import br.com.bandtec.gespo.model.Project
import br.com.bandtec.gespo.requests.ProjectRequest
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_time_entry.*
import kotlinx.android.synthetic.main.activity_timesheet_consult.*
import kotlinx.android.synthetic.main.activity_timesheet_consult.sp_projeto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TimeEntryActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    val costCenters = mutableListOf<String>()

    val projects = mutableListOf<String>()
  
    var estadoFiltro = false

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_entry)

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_apontamentos

        val api = Retrofit.Builder()
            .baseUrl("https://gespo-rest.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val projectRequest = api.create(ProjectRequest::class.java)

        val getProject = projectRequest.getProjectsByEmployee(cookie,id)

        getProject.enqueue(object: Callback<List<Project>> {
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<List<Project>>,
                response: Response<List<Project>>
            ) {
                response.body()?.forEach{project ->
                        projects.add(project.name)
                        costCenters.add(project.costCenter.name)

                }
                sp_projeto.adapter = ArrayAdapter(applicationContext,
                    R.layout.support_simple_spinner_dropdown_item,
                    projects)

                sp_costCenter.adapter = ArrayAdapter(applicationContext,
                        R.layout.support_simple_spinner_dropdown_item,
                        costCenters)
            }
        })


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