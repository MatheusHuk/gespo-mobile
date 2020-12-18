package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.model.CostCenter
import br.com.bandtec.gespo.model.Employee
import br.com.bandtec.gespo.model.Project
import br.com.bandtec.gespo.model.TimeEntryDTO
import br.com.bandtec.gespo.requests.EmployeeRequest
import br.com.bandtec.gespo.requests.ProjectRequest
import br.com.bandtec.gespo.requests.TimeEntryRequest
import br.com.bandtec.gespo.utils.changeActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_time_entry.*
import kotlinx.android.synthetic.main.activity_time_entry.cl_tela_inteira
import kotlinx.android.synthetic.main.activity_time_entry.et_data
import kotlinx.android.synthetic.main.activity_time_entry.loading
import kotlinx.android.synthetic.main.activity_time_entry.loadingImage
import kotlinx.android.synthetic.main.activity_time_entry.sp_projeto
import kotlinx.android.synthetic.main.activity_time_entry.tv_username
import kotlinx.android.synthetic.main.activity_time_entry.v_fundo_form_bottom
import kotlinx.android.synthetic.main.activity_time_entry.v_fundo_form_top
import kotlinx.android.synthetic.main.activity_timesheet_consult.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TimeEntryActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    var costCenters : Array<String> = emptyArray()

    var projects : Array<String> = emptyArray()

    val projectsList = mutableListOf<Project>()
  
    var estadoFiltro = false

    var user : Employee? = null

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    val api = Retrofit.Builder()
        .baseUrl("https://gespo-rest.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val projectRequest = api.create(ProjectRequest::class.java)

    val timeEntryRequest = api.create(TimeEntryRequest::class.java)

    val employeeRequest = api.create(EmployeeRequest::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_entry)

        Glide.with(this)
            .load(R.mipmap.ring)
            .asGif()
            .into(loadingImage);

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        tv_username.text = name

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_apontamentos

        val hou = intent.extras?.getInt("hours")
        val min = intent.extras?.getInt("minutes")

        if(hou != null && min != null){
            et_hora.setText(hou.toString())
            et_minuto.setText(min.toString())
        }

        et_data.addTextChangedListener(object: TextWatcher {
            var isUpdating = false
            var oldString = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = s.toString().replace("/", "")
                val mask = "##/##/####"
                var newString = ""

                if (count == 0)//is deleting
                    isUpdating = true

                if (isUpdating){
                    oldString = str
                    isUpdating = false
                    return
                }

                var i = 0
                for (m : Char in mask.toCharArray()){
                    if (!m.equals('#') && str.length > oldString.length){
                        newString += m
                        continue
                    }
                    try {
                        newString += str.get(i)
                    }catch (e : Exception){
                        break
                    }
                    i++
                }

                isUpdating = true
                et_data.setText(newString)
                et_data.setSelection(newString.length)

            }
        })

        val getProject = projectRequest.getProjectsByEmployee(cookie,id)

        getProject.enqueue(object: Callback<List<Project>> {
            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<List<Project>>,
                response: Response<List<Project>>
            ) {
                projects = response.body()!!.map { p -> p.name }.toTypedArray()
                costCenters = response.body()!!.map { p -> p.costCenter.name }.toTypedArray()

                response.body()?.forEach{project ->
                        projectsList.add(project)
                }
                sp_projeto.adapter = ArrayAdapter(applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    projects)

                sp_costCenter.adapter = ArrayAdapter(applicationContext,
                    android.R.layout.simple_spinner_dropdown_item,
                    costCenters)

                val getEmployee = employeeRequest.getEmployee(cookie, id)

                getEmployee.enqueue(object : Callback<Employee>{
                    override fun onFailure(call: Call<Employee>, t: Throwable) {
                        Toast.makeText(baseContext, "Erro: $t", Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(
                        call: Call<Employee>,
                        response: Response<Employee>

                    ) {

                        user = response.body()!!
                        val selectedCostCenter: String = costCenters.get(0)

                        projects = projectsList.filter { p -> p.costCenter.name.equals(selectedCostCenter) }
                            .map{ p -> p.name }.toTypedArray()

                        sp_projeto.adapter = ArrayAdapter(applicationContext,
                            android.R.layout.simple_spinner_dropdown_item,
                            projects)

                        sp_projeto.setSelection(0)

                        et_projetao_pt.text = projects.get(0)
                        et_manager.text = projectsList.filter { p -> p.name.equals(projects.get(0)) }.get(0).manager.name

                        loading.visibility = View.GONE
                        cl_tela_inteira.visibility = View.VISIBLE

                    }
                })

                sp_projeto.setOnItemSelectedListener(
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            et_projetao_pt.text = sp_projeto.selectedItem.toString()
                            val project:Project = projectsList.filter { proj -> proj.name.equals(et_projetao_pt.text)}.first()
                            et_manager.text = project.manager.name.toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }
                )

                sp_costCenter.setOnItemSelectedListener(
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selected = sp_costCenter.selectedItem.toString()

                            projects = projectsList.filter { p -> p.costCenter.name.equals(selected) }
                                .map { p -> p.name }.toTypedArray()

                            sp_projeto.adapter = ArrayAdapter(applicationContext,
                                android.R.layout.simple_spinner_dropdown_item,
                                projects)

                            sp_projeto.setSelection(0)

                            et_projetao_pt.text = projects.get(0)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }
                )
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


    fun logOff(v:View) {
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


    fun gravarApontamento(v:View){
        loading.visibility = View.VISIBLE
        cl_tela_inteira.visibility = View.GONE

        // 10/12/2020
        val dataBruta = et_data.text.toString()
        val creationDate: IntArray = intArrayOf(dataBruta.substring(6,10).toInt(), dataBruta.substring(3,5).toInt(), dataBruta.substring(0,2).toInt())
        val amountHours = (et_hora.text.toString().toInt() + (et_minuto.text.toString().toInt()/60)).toDouble()
        val project:Project = projectsList.filter { proj -> proj.name.equals(et_projetao_pt.text)}.first()
        val dswork = et_observacao.text.toString()

        val timeEntry = TimeEntryDTO(user!!, creationDate, amountHours, project, dswork)
        val callCreateTimeEntry = timeEntryRequest.createTimeEntry(cookie, mutableListOf<TimeEntryDTO>(timeEntry))


        callCreateTimeEntry.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(baseContext, "Erro: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(baseContext, "Apontamento criado com sucesso!", Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
                cl_tela_inteira.visibility = View.VISIBLE
            }

        })



    }
  
    override fun onBackPressed() {
        val timesheetConsultActivity = Intent(this, TimesheetConsultActivity::class.java)
        startActivity(timesheetConsultActivity)
    }

}