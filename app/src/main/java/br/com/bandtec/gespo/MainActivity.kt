package br.com.bandtec.gespo

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val chartView2: PieChart = PieChart(this)
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

        var set: ConstraintSet = ConstraintSet()

        set.connect(chartView2.id, ConstraintSet.TOP, chartView.id, ConstraintSet.BOTTOM)
       //set.applyTo(chartView2)

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

        appContainer.addView(chartView)
        appContainer.addView(chartView2)
    }
}