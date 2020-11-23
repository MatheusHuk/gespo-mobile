package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout.TEXT_ALIGNMENT_CENTER
import androidx.core.view.marginTop
import br.com.bandtec.gespo.model.Project
import br.com.bandtec.gespo.model.TimeEntry
import br.com.bandtec.gespo.model.dashboards.ManagerDashOne
import br.com.bandtec.gespo.requests.ProjectRequest
import br.com.bandtec.gespo.requests.TimeEntryRequest
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timesheet_consult.*
import org.w3c.dom.Text
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

            val timeEntryRequest = api.create(TimeEntryRequest::class.java)

            val getTimeEntry = timeEntryRequest.getTimeEntriesByEmployee(cookie,id)

            getTimeEntry.enqueue(object:Callback<List<TimeEntry>>{
                override fun onFailure(call: Call<List<TimeEntry>>, t: Throwable) {
                    Toast.makeText(applicationContext, "deu ruim", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<List<TimeEntry>>,
                    response: Response<List<TimeEntry>>
                ) {
                    Toast.makeText(applicationContext, "code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    response.body()?.forEach{timeEntry ->
                        val tblRow = TableRow(applicationContext)

                        val txtProject = TextView(applicationContext)
                        val txtDate = TextView(applicationContext)
                        val txtHours = TextView(applicationContext)
                        val btDelete = Button(applicationContext)

                        val tableRowParams = TableRow.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            (applicationContext.getResources().getDisplayMetrics().density * 10).toInt()

                        )

                        val textViewParams = TableRow.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        )

                        textViewParams.gravity = Gravity.CENTER

                        tblRow.layoutParams = tableRowParams
                        txtProject.layoutParams = textViewParams
                        txtDate.layoutParams = textViewParams
                        txtHours.layoutParams = textViewParams
                        btDelete.layoutParams = textViewParams

                        txtProject.text = timeEntry.project.name
                        txtProject.setTextSize((TypedValue.COMPLEX_UNIT_SP * 8).toFloat())
                        txtProject.setTextColor(Color.BLACK)

                        txtDate.text = timeEntry.creationDate.toString()
                        txtDate.setTextSize((TypedValue.COMPLEX_UNIT_SP * 8).toFloat())
                        txtDate.setTextColor(Color.BLACK)

                        txtHours.text = timeEntry.amountHours.toString()
                        txtHours.setTextSize((TypedValue.COMPLEX_UNIT_SP * 8).toFloat())
                        txtHours.setTextColor(Color.BLACK)

                        btDelete.text = "teste"
                        btDelete.setTextSize((TypedValue.COMPLEX_UNIT_SP * 8).toFloat())
                        btDelete.setTextColor(Color.BLACK)

                        tblRow.addView(txtProject)
                        tblRow.addView(txtDate)
                        tblRow.addView(txtHours)
                        tblRow.addView(btDelete)

                        tl_tabela_consulta.addView(tblRow)
                    }
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
        params.topMargin = TypedValue.COMPLEX_UNIT_DIP * 510
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