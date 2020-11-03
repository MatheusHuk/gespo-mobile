package br.com.bandtec.gespo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.model.Employee
import br.com.bandtec.gespo.model.dashboards.ManagerDashOne
import br.com.bandtec.gespo.model.dashboards.ManagerDashThree
import br.com.bandtec.gespo.model.dashboards.ManagerDashTwo
import br.com.bandtec.gespo.requests.AuthRequest
import br.com.bandtec.gespo.requests.DashRequest
import br.com.bandtec.gespo.utils.changeActivity
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.loading
import kotlinx.android.synthetic.main.activity_main.loadingImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    val api = Retrofit.Builder()
        .baseUrl("https://gespo-rest.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val dashApi = Retrofit.Builder()
        .baseUrl("https://gespo-dash-back.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val employeeRequest = api.create(AuthRequest::class.java)

    val dashRequest = dashApi.create(DashRequest::class.java)

    var cookie:String = ""
    var id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Glide.with(this)
            .load(R.mipmap.ring)
            .asGif()
            .into(loadingImage);

        cookie = intent.extras!!.get("cookie").toString()
        id = intent.extras!!.getInt("id")
        val name = intent.extras!!.get("username").toString()

        tv_username.text = name

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_dashboard

        navView.setOnNavigationItemSelectedListener(
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            changeActivity(item ,this.applicationContext)
            return@OnNavigationItemSelectedListener true
        })

        val getUser = employeeRequest.getEmployee(cookie, id)

        getUser.enqueue(object: Callback<Employee> {
            override fun onFailure(call: Call<Employee>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<Employee>, response: Response<Employee>) {
                val permission = response.body()?.office?.permission?.id

                if(permission !== 1){
                    mountManagerDashOne(){ resp ->
                        mountManagerDashTwo(){ resp ->
                            mountManagerDashThree(){ resp ->
                                loading.visibility = View.GONE
                                app_scroll_view.visibility = View.VISIBLE
                            }
                        }
                    }
                }else{
                    println("IS NOT MANAGER")
                }
            }
        })


    }

    fun mountManagerDashOne(callback: (Boolean) -> Unit){

        //Manager First Dash
        val getFirstDash = dashRequest.getManagerDashOne()

        getFirstDash.enqueue(object: Callback<ManagerDashOne> {
            override fun onFailure(call: Call<ManagerDashOne>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<ManagerDashOne>,
                response: Response<ManagerDashOne>
            ) {
                val dashOne: BarChart = BarChart(applicationContext)

                val barEntries = ArrayList<BarEntry>(0)

                var cont: Float = 0f
                var entryList = ArrayList<String>()

                response.body()?.data?.forEach { data ->
                    if(data.projectName == null)
                        return@forEach
                    entryList.add(data.projectName)
                    val values:FloatArray = floatArrayOf(data.totalAmountWork.toFloat(),data.totalAmountProvisioning.toFloat())
                    barEntries.add(BarEntry(cont, values))
                    cont += 1
                }
                val barSet = BarDataSet(barEntries, "")

                barSet.isVisible = true
                barSet.setDrawIcons(true)
                barSet.iconsOffset = MPPointF(0F, 40F)
                barSet.setColors(mutableListOf(Color.BLUE, Color.CYAN))
                barSet.valueTextColor = Color.TRANSPARENT
                barSet.stackLabels = arrayOf("Apontando em Dinheiro", "Provisionado em Dinheiro")

                dashOne.description.isEnabled = false
                dashOne.description.textAlign = Paint.Align.CENTER

                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
                )

                dashOne.layoutParams = params
                dashOne.data = BarData(barSet)
                dashOne.xAxis.valueFormatter = IndexAxisValueFormatter(entryList)
                dashOne.xAxis.position = XAxis.XAxisPosition.BOTTOM
                dashOne.xAxis.labelRotationAngle = 45f
                dashOne.invalidate()

                val l = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                l.bottomMargin = 100

                val textView = TextView(applicationContext)
                textView.text = "Apontamento X provisionamento em Dinheiro por Projeto"
                textView.layoutParams = l
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

                appContainer.addView(dashOne)
                appContainer.addView(textView)

                callback.invoke(true)
            }
        })
    }

    fun mountManagerDashTwo(callback: (Boolean) -> Unit) {

        //Manager Second Dash
        val getSecondDash = dashRequest.getManagerDashTwo()

        getSecondDash.enqueue(object: Callback<ManagerDashTwo> {
            override fun onFailure(call: Call<ManagerDashTwo>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<ManagerDashTwo>,
                response: Response<ManagerDashTwo>
            ) {
                val dashTwo: BarChart = BarChart(applicationContext)

                val barEntries = ArrayList<BarEntry>(0)

                var cont: Float = 0f
                var entryList = ArrayList<String>()

                response.body()?.data?.forEach { data ->
                    if(data.projectName == null)
                        return@forEach
                    entryList.add(data.projectName)
                    val values:FloatArray = floatArrayOf(data.totalHoursWork.toFloat(),data.totalHoursProvisioning.toFloat())
                    barEntries.add(BarEntry(cont, values))
                    cont += 1
                }

                val barSet = BarDataSet(barEntries, "")

                barSet.isVisible = true
                barSet.setDrawIcons(true)
                barSet.iconsOffset = MPPointF(0F, 40F)
                barSet.setColors(mutableListOf(Color.BLUE, Color.CYAN))
                barSet.valueTextColor = Color.TRANSPARENT
                barSet.stackLabels = arrayOf("Apontando em Horas", "Provisionado em Horas")

                dashTwo.description.isEnabled = false
                dashTwo.description.textAlign = Paint.Align.CENTER

                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
                )

                dashTwo.layoutParams = params
                dashTwo.data = BarData(barSet)
                dashTwo.xAxis.valueFormatter = IndexAxisValueFormatter(entryList)
                dashTwo.xAxis.position = XAxis.XAxisPosition.BOTTOM
                dashTwo.xAxis.labelRotationAngle = 45f
                dashTwo.invalidate()

                val l = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                l.bottomMargin = 100

                val textView = TextView(applicationContext)
                textView.text = "Apontamento X provisionamento em Horas por Projeto"
                textView.layoutParams = l
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

                appContainer.addView(dashTwo)
                appContainer.addView(textView)

                callback.invoke(true)
            }
        })
    }

    fun mountManagerDashThree(callback: (Boolean) -> Unit){

        //Manager Third Dash
        val getThirdDash = dashRequest.getManagerDashThree()

        getThirdDash.enqueue( object: Callback<ManagerDashThree> {
            override fun onFailure(call: Call<ManagerDashThree>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(
                call: Call<ManagerDashThree>,
                response: Response<ManagerDashThree>
            ) {
                val dashThree: PieChart = PieChart(applicationContext)

                val pieEntries = ArrayList<PieEntry>(0)

                var cont: Float = 0f
                var entryList = ArrayList<String>()

                response.body()?.data?.forEach { data ->
                    if(data.projectName == null)
                        return@forEach
                    entryList.add(data.projectName)
                    pieEntries.add(PieEntry(data.employeeCount.toFloat(), data.projectName))
                    cont += 1
                }

                val dataSet = PieDataSet(pieEntries, "")

                dataSet.isVisible = true
                dataSet.setDrawIcons(true)
                dataSet.sliceSpace = 3f
                dataSet.iconsOffset = MPPointF(0F, 40F)
                dataSet.selectionShift = 5f
                dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 20f

                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
                )

                dashThree.layoutParams = params
                dashThree.holeRadius = 0f
                dashThree.transparentCircleRadius = 0f
                dashThree.legend.isEnabled = false
                dashThree.description.isEnabled = true
                dashThree.description.setPosition(0F,40F)
                dashThree.setData(PieData(dataSet))
                dashThree.setEntryLabelColor(Color.BLACK)
                dashThree.invalidate()

                val l = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                l.bottomMargin = 100

                val textView = TextView(applicationContext)
                textView.text = "Quantidade de funcionários por Projeto"
                textView.layoutParams = l
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

                appContainer.addView(dashThree)
                appContainer.addView(textView)

                callback.invoke(true)
            }
        })

    }

    fun mountEmployeeDash(){

    }
}