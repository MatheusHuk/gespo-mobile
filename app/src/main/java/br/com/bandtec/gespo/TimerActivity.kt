package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_timer.*
import java.time.LocalDateTime
import java.util.*
import kotlin.math.truncate

class TimerActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0
    private var timestamp: Long = 0
    private var istimerRunning = false

    val timer = object : CountDownTimer((24 * 60 * 60 * 1000), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            addSecond()
        }

        override fun onFinish() {
            TODO("Not yet implemented")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        //esse é o código do spinner
        //ele ta puxando lá do arquivo strings.xml as opções
        sp_options.adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.options)
        )

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item, this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)
        checkTimeStamp()
    }

    fun abrirFiltro(v: View) {

    }

    fun startTimer(v: View) {
        if (!istimerRunning) {
            timer.start()
            istimerRunning = true
            if (timestamp.equals(0L)) {
                timestamp = System.currentTimeMillis()

                val editor = preferences?.edit()
                editor?.putLong("timerTimestamp", timestamp)
                editor?.putBoolean("timerIsRunning", true)

                editor?.commit()
            }
            fab_play.isEnabled = false
            fab_pause.isEnabled = true
            fab_stop.isEnabled = true
        }
    }

    fun pauseTimer(v: View) {
        if (istimerRunning) {
            timer.cancel()
            istimerRunning = false
            fab_play.isEnabled = true
            fab_pause.isEnabled = false
            fab_stop.isEnabled = true

            val editor = preferences?.edit()
            editor?.remove("timerIsRunning")
            editor?.commit()
        }
    }

    fun stopTimer(v: View) {
        if (istimerRunning) {
            timer.cancel()
            istimerRunning = false
        }
        val editor = preferences?.edit()
        editor?.remove("timerTimestamp")
        editor?.remove("timerIsRunning")
        editor?.commit()

        val timeEntryActivity = Intent(this, TimeEntryActivity::class.java)
        startActivity(timeEntryActivity)
    }

    fun addSecond() {
        if(this.hours == 24){
            return
        }
        this.seconds++
        if (this.seconds >= 60) {
            this.minutes++
            this.seconds = 0
            if (this.minutes >= 60) {
                this.hours++
                this.minutes = 0
            }
        }

        convertAndPutOnView()
    }

    fun convertAndPutOnView() {
        val realSeconds: String = if(this.seconds < 10) "0"+this.seconds else this.seconds.toString()
        val realMinutes: String = if(this.minutes < 10) "0"+this.minutes else this.minutes.toString()
        val realHours: String = if(this.hours < 10) "0"+this.hours else this.hours.toString()

        tv_seconds.text = realSeconds
        tv_minutes.text = realMinutes
        tv_hours.text = realHours
    }

    fun checkTimeStamp(){
        val TS = preferences?.getLong("timerTimestamp", 0)
        if(TS != 0L){
            this.timestamp = TS!!
            val currentTime = System.currentTimeMillis()
            calculateTimestamp(timestamp, currentTime)
        }
    }

    fun calculateTimestamp(start: Long, actual: Long){
        val difference = truncate(((actual - start) / 1000).toDouble()).toInt()
        this.seconds = difference % 60

        val min = truncate((difference / 60).toDouble()).toInt()
        this.minutes = min % 60

        val hours = truncate((min / 60).toDouble()).toInt()
        this.hours = hours

        convertAndPutOnView()

        val isRunning = preferences!!.getBoolean("timerIsRunning", false)

        if(isRunning)
            startTimer(fab_play)
    }
}