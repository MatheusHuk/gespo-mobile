package br.com.bandtec.gespo

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*

class TimerActivity : AppCompatActivity(){

    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0
    private var istimerRunning = false

    val timer = object: CountDownTimer((24 * 60 * 60 * 1000), 1000){
        override fun onTick(millisUntilFinished: Long) {
            addSecond()
        }

        override fun onFinish() {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        //esse é o código do spinner
        //ele ta puxando lá do arquivo strings.xml as opções
        sp_options.adapter = ArrayAdapter(this,
        R.layout.support_simple_spinner_dropdown_item,
        resources.getStringArray(R.array.options))

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })
    }

    fun abrirFiltro(v: View){

    }

    fun startTimer(v:View){
        if(!istimerRunning){
            timer.start()
            istimerRunning = true
        }
    }

    fun stopTimer(v:View){
        if(istimerRunning){
            timer.cancel()
            istimerRunning = false
        }
    }

    fun addSecond(){
        this.seconds++
        if(this.seconds >= 60){
            this.minutes++
            this.seconds = 0
            if(this.minutes >= 60){
                this.hours++
                this.minutes = 0
            }
        }

        convertAndPutOnView(this.seconds, tv_seconds)
        convertAndPutOnView(this.minutes, tv_minutes)
        convertAndPutOnView(this.hours, tv_hours)
    }

    fun convertAndPutOnView(value: Int, view: TextView){
        var stringValue = value.toString()
        if(value < 10)
            stringValue = "0"+stringValue
        view.text = stringValue
    }
}