package br.com.bandtec.gespo

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Layout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import br.com.bandtec.gespo.utils.changeActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginBottom
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import java.text.AttributedCharacterIterator

class MainActivity : AppCompatActivity() {

    var nome:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nome = intent.extras?.get("username").toString()
        tv_username.text = nome

        val c:ColorTemplate = ColorTemplate()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_dashboard

        navView.setOnNavigationItemSelectedListener(
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            changeActivity(item ,this.applicationContext)
            return@OnNavigationItemSelectedListener true
        })

        //val chartView: PieChart = findViewById(R.id.chart)
        val entries = ArrayList<PieEntry>(0)
        //val container: View = findViewById(R.id.appContainer)

        //val chartArray: ArrayList<PieChart> = ArrayList<PieChart>()
        //chartArray.add(PieChart(this))

        val chartView: PieChart = PieChart(this)
        //appContainer.addView(newView)

        entries.add(PieEntry(50f, "Brazil"))
        entries.add(PieEntry(75f, "USA"))
        entries.add(PieEntry(37f, "Germany"))
        entries.add(PieEntry(37f, "France"))
        entries.add(PieEntry(37f, "Italy"))
        entries.add(PieEntry(37f, "Japan"))
        entries.add(PieEntry(37f, "Mexico"))
        entries.add(PieEntry(37f, "UK"))
        entries.add(PieEntry(37f, "Canada"))
        entries.add(PieEntry(37f, "Australia"))

        val dataSet = PieDataSet(entries, "")

        dataSet.isVisible = true
        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextColor = Color.TRANSPARENT

        val data = PieData(dataSet)

        chartView.holeRadius = 0f
        chartView.transparentCircleRadius = 0f
        chartView.legend.isEnabled = false
        chartView.description.isEnabled = false
        chartView.description.textAlign = Paint.Align.CENTER
        chartView.setData(data)
        chartView.invalidate()

        chartView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            (this.applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
        )

        val l = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        l.bottomMargin = 100

        var textView = TextView(this)
        textView.text = "Gráfico Foda 1"
        textView.layoutParams = l
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        appContainer.addView(chartView)
        appContainer.addView(textView)


        val chartView2: PieChart = PieChart(this)

        chartView2.holeRadius = 0f
        chartView2.transparentCircleRadius = 0f
        chartView2.legend.isEnabled = false
        chartView2.description.isEnabled = false
        chartView2.description.textAlign = Paint.Align.CENTER
        chartView2.setData(data)
        chartView2.invalidate()

        chartView2.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            (this.applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
        )

        val textView2 = TextView(this)
        textView2.text = "Gráfico Foda 2"
        textView2.layoutParams = l
        textView2.textAlignment = View.TEXT_ALIGNMENT_CENTER


        appContainer.addView(chartView2)
        appContainer.addView(textView2)

        val chartView3: BarChart = BarChart(this)

        val barEntries = ArrayList<BarEntry>(0)

        barEntries.add(BarEntry(1f, 10f))
        barEntries.add(BarEntry(2f, 15f))
        barEntries.add(BarEntry(3f, 20f))
        barEntries.add(BarEntry(4f, 17f))
        barEntries.add(BarEntry(5f, 19f))
        barEntries.add(BarEntry(6f, 21f))

        val barSet: BarDataSet = BarDataSet(barEntries, "")

        barSet.isVisible = true
        barSet.setDrawIcons(true)
        barSet.iconsOffset = MPPointF(0F, 40F)
        barSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        barSet.valueTextColor = Color.TRANSPARENT

        chartView3.legend.isEnabled = false
        chartView3.description.isEnabled = false
        chartView3.description.textAlign = Paint.Align.CENTER
        chartView3.setData(BarData(barSet))
        chartView3.invalidate()

        chartView3.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            (this.applicationContext.getResources().getDisplayMetrics().density * 400).toInt()
        )

        val textView3 = TextView(this)
        textView3.text = "Gráfico Foda, agora em barras"
        textView3.layoutParams = l
        textView3.textAlignment = View.TEXT_ALIGNMENT_CENTER

        appContainer.addView(chartView3)
        appContainer.addView(textView3)
    }
}