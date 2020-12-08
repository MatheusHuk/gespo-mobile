package br.com.bandtec.gespo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.activity_timer.loading
import kotlinx.android.synthetic.main.activity_timer.tv_username
import kotlin.math.truncate

class TimerActivity : AppCompatActivity() {

    var preferences: SharedPreferences? = null

    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0
    private var timerTimestamp: Long = 0
    private var timerIsRunning = false
    private var timerSum: Long = 0

    val timer = object : CountDownTimer((24 * 60 * 60 * 1000), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            addSecond()
        }

        override fun onFinish() {
            TODO("Not yet implemented")
        }
    }
    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        sp_options.adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.options)
        )

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        tv_username.text = name

        //esse é o código do spinner
        //ele ta puxando lá do arquivo strings.xml as opções
        sp_options.adapter = ArrayAdapter(this,
        R.layout.support_simple_spinner_dropdown_item,
        resources.getStringArray(R.array.options))

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item, this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })

        sp_options.setSelection(1)
        sp_options.setOnItemSelectedListener(
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(id.equals(0L)){
                        val intent = Intent(applicationContext, CounterActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        )

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)
        checkTimeStamp()
    }

    override fun onBackPressed() {
        //Crime ocorre, nada acontece Feijoada
    }

    fun startTimer(v: View) {
        this.timer.start()
        this.timerIsRunning = true
        this.timerTimestamp = System.currentTimeMillis()
        this.normalizePreferences()
    }

    fun pauseTimer(v: View) {
        this.timer.cancel()
        this.timerIsRunning = false
        this.timerSum = (this.seconds * 1000 + (this.minutes * 60000) + (this.hours * 3600000)).toLong()
        this.normalizePreferences()
    }

    fun stopTimer(v: View) {
        this.timer.cancel()
        this.timerIsRunning = false
        this.timerTimestamp = 0L
        this.timerSum = 0L
        this.normalizePreferences()
    }

    fun resetTimer(v: View) {
        this.timer.cancel()
        this.timerIsRunning = false
        this.timerTimestamp = 0L
        this.timerSum = 0L
        this.hours = 0
        this.minutes = 0
        this.seconds = 0
        this.convertAndPutOnView()
        this.normalizePreferences()
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
        this.timerTimestamp = preferences!!.getLong("timerTimestamp", 0)
        this.timerIsRunning = preferences!!.getBoolean("timerIsRunning", false)
        this.timerSum = preferences!!.getLong("timerSum", 0)

        if(!timerTimestamp.equals(0L)){
            if(timerIsRunning){
                if(timerSum.equals(0L)){
                    calculateTimestamp((System.currentTimeMillis() - timerTimestamp))
                }else{
                    calculateTimestamp(timerSum + (System.currentTimeMillis() - timerTimestamp))
                }
            }else{
                calculateTimestamp(timerSum)
            }
        }
    }

    fun calculateTimestamp(diff: Long){
        val difference = truncate((diff / 1000).toDouble()).toInt()
        this.seconds = difference % 60

        val min = truncate((difference / 60).toDouble()).toInt()
        this.minutes = min % 60

        val hours = truncate((min / 60).toDouble()).toInt()
        this.hours = hours

        convertAndPutOnView()

        if(this.timerIsRunning)
            startTimer(fab_play)
    }

    fun normalizePreferences() {
        val editor = preferences!!.edit()

        editor.putLong("timerTimestamp", this.timerTimestamp)
        editor.putBoolean("timerIsRunning", this.timerIsRunning)
        editor.putLong("timerSum", this.timerSum)

        editor.commit()
    }

    fun logOff(v:View){
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
}