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
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timesheet_consult.*
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TimesheetConsultActivity : AppCompatActivity() {

    lateinit var projetoSelecionado: Spinner

    var qtdTotalDeHoras = 0.0

    var preferences: SharedPreferences? = null

    var projects: Array<String> = emptyArray()

    var projectList = mutableListOf<Project>()

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    var estadoFiltro = false

    val api = Retrofit.Builder()
        .baseUrl("https://gespo-rest.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val projectRequest = api.create(ProjectRequest::class.java)

    val timeEntryRequest = api.create(TimeEntryRequest::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_consult)

        Glide.with(this)
            .load(R.mipmap.ring)
            .asGif()
            .into(loadingImage);

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

//            sp_periodo.adapter = ArrayAdapter(this,
//            R.layout.support_simple_spinner_dropdown_item,
//            resources.getStringArray(R.array.periodos))

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
                        projects = response.body()!!.map { project -> project.name }.toTypedArray()
                        projectList.add(project)
                    }

                    projetoSelecionado = findViewById(R.id.sp_projeto)
                    val adapter = ArrayAdapter(
                        applicationContext,
                        R.layout.support_simple_spinner_dropdown_item,
                        projects
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    projetoSelecionado.adapter = adapter
                    projetoSelecionado.onItemSelectedListener = sp_projeto.onItemSelectedListener



//                    sp_projeto.adapter = ArrayAdapter(applicationContext,
//                    R.layout.support_simple_spinner_dropdown_item,
//                    projects)
                }
            })

            val getTimeEntry = timeEntryRequest.getTimeEntriesByEmployee(cookie,id)

            getTimeEntry.enqueue(object:Callback<List<TimeEntry>>{
                override fun onFailure(call: Call<List<TimeEntry>>, t: Throwable) {
                    Toast.makeText(applicationContext, "Algo de errado aconteceu !", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<List<TimeEntry>>,
                    response: Response<List<TimeEntry>>
                ) {
                    response.body()?.forEach{timeEntry ->
                        val tblRow = TableRow(applicationContext)

                        val txtProject = TextView(applicationContext)
                        val txtDate = TextView(applicationContext)
                        val txtHours = TextView(applicationContext)
                        val btDelete = ImageButton(applicationContext)

                        val tableRowParams = TableRow.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )

                        val textViewParams = TableRow.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT
                        )

                        tblRow.layoutParams = tableRowParams
                        txtProject.layoutParams = textViewParams
                        txtDate.layoutParams = textViewParams
                        txtHours.layoutParams = textViewParams
                        btDelete.layoutParams = textViewParams

                        txtProject.text = timeEntry.project.name
                        txtProject.setTextSize((TypedValue.COMPLEX_UNIT_SP * 10.75).toFloat())
                        txtProject.setTextColor(Color.BLACK)
                        txtProject.gravity = Gravity.CENTER

                        txtDate.text = "${timeEntry.creationDate[2]}/${timeEntry.creationDate[1]}/${timeEntry.creationDate[0]}"
                        txtDate.setTextSize((TypedValue.COMPLEX_UNIT_SP * 10.75).toFloat())
                        txtDate.setTextColor(Color.BLACK)
                        txtDate.gravity = Gravity.CENTER

                        txtHours.text = timeEntry.amountHours.toString()
                        txtHours.setTextSize((TypedValue.COMPLEX_UNIT_SP * 10.75).toFloat())
                        txtHours.setTextColor(Color.BLACK)
                        txtHours.gravity = Gravity.CENTER

                        qtdTotalDeHoras += timeEntry.amountHours

                        btDelete.setImageResource(R.drawable.ic_baseline_delete_18);
                        btDelete.id = timeEntry.id

                        btDelete.setOnClickListener{ view -> deleteTimeEntry(view)}
                        //btDelete.setBackgroundColor(Color.parseColor("#7A7A7A"))

                        tblRow.addView(txtProject)
                        tblRow.addView(txtDate)
                        tblRow.addView(txtHours)
                        tblRow.addView(btDelete)

                        tl_tabela_consulta.addView(tblRow)
                    }
                    tv_qtd_hora_total.text = qtdTotalDeHoras.toString()

                    loading.visibility = View.GONE
                    cl_tela_inteira.visibility = View.VISIBLE
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
            params.topMargin = (applicationContext.getResources().getDisplayMetrics().density * 130).toInt()
            sv_scroll.layoutParams = params

            sv_scroll.invalidate()
            sv_scroll.requestLayout()

            tv_txt_detalhes.visibility = View.VISIBLE

            estadoFiltro = false
        }
        else{
        //ajustando o table layout na tela para se adequar as mudanças
        //criando uma variável do tipo Layout Params
        params.topMargin = (applicationContext.getResources().getDisplayMetrics().density * 260).toInt()
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

    fun deleteTimeEntry(v:View){

        val deleteTimeEntry = timeEntryRequest.deleteTimeEntryById(cookie,v.id)

        deleteTimeEntry.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Algo de errado aconteceu !", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT).show()

                val timeConsultActivity = Intent(applicationContext, TimesheetConsultActivity::class.java)
                startActivity(timeConsultActivity)
            }
        })
    }

//    fun filterTimeEntry(v:View){
//        val filterTimeEntry = timeEntryRequest.getTimeEntriesByFilters(cookie,)
//    }

}