package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.content.*
import android.os.IBinder
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.services.CounterService
import br.com.bandtec.gespo.services.broadcasts.CounterBroadcastReceiver
import br.com.bandtec.gespo.services.broadcasts.CounterNotifyBroadcastReceiver
import br.com.bandtec.gespo.services.listeners.CounterListener
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_counter.*
import kotlin.math.truncate
import kotlinx.android.synthetic.main.activity_counter.loading
import kotlinx.android.synthetic.main.activity_counter.tv_username

class CounterActivity : AppCompatActivity() {

    private lateinit var CounterSvcController: CounterListener

    private var counterBroadcastReceiver: CounterBroadcastReceiver? = null
    private var counterNotifyBroadcastReceiver: CounterNotifyBroadcastReceiver? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as CounterService.CounterSvcController
            CounterSvcController = binder.getCountListener()
            setEnableScrollNumberPickers(!CounterSvcController.isActive())
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            // Feijoada
        }
    }

    companion object {
        var inst: CounterActivity? = null
        fun getInstance(): CounterActivity? {
            return inst
        }
    }

    var cookie: String = ""
    var name: String = ""
    var id: Int = 0

    var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cria a instância
        inst = this

        setContentView(R.layout.activity_counter)

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        tv_username.text = name

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        //Esse é o código do spinner
        //Ele ta puxando lá do arquivo strings.xml as opções
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

        // BIND!! Serve para termos o objeto CONTROLLER do serviço (E poder recuperar os valores)
        this.bindCounterService()

        // Criaçao do BroadcastReceiver que se mantém ouvindo para notificar o usuário
        this.registerBroadcastNotify()

        // Criar o Broadcast para mudar o Front
        this.registerBroadcastFront()

        // Funçao que configura as "NumberPickers" com min/max Value
        this.configureNumberPickers()

        loading.visibility = View.GONE
        cl_tela_inteira.visibility = View.VISIBLE
    }

    fun logOff(v: View) {
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

    override fun onDestroy() {
        super.onDestroy()

        // Por fins de performance, nao e interessante manter essa sessao do Bind.
        unbindService(this.serviceConnection)

        // Parar de "atualizar o front"
        unregisterReceiver(this.counterBroadcastReceiver)
    }

    fun start(view: View) {
        val seconds: Int = this.getSecondsOnView()

        if (seconds != 0) {
            this.setEnableScrollNumberPickers(false)
            this.startService(seconds)
        } else
            Toast.makeText(this, "Favor selecionar um número válido", Toast.LENGTH_SHORT).show()
    }

    fun pause(view: View) {
        this.stopService()
    }

    fun stop(view: View) {
        this.stopService()

        this.CounterSvcController.setCounterTimer(0)

        this.convertSecondsToStringTimerAndPutInView(0)
        this.setEnableScrollNumberPickers(true)
    }

    fun finish(view: View) {
        this.pause(view)

        val seconds: Int = this.CounterSvcController.getStartTime() - this.getSecondsOnView()

        val minutesDiff: Int = truncate((seconds / 60).toDouble()).toInt()
        val minutesFinal: Int = minutesDiff % 60

        val hoursDiff: Int = truncate((minutesDiff / 60).toDouble()).toInt()
        val hoursFinal: Int = hoursDiff

        val timeEntryActivity = Intent(this, TimeEntryActivity::class.java)
        timeEntryActivity.putExtra("hours", hoursFinal)
        timeEntryActivity.putExtra("minutes", minutesFinal)

        startActivity(timeEntryActivity)
    }


    private fun startService(seconds: Int) {
        Intent(this, CounterService::class.java).also { intent ->
            intent.action = "GESPO_COUNTER_SERVICE"
            intent.putExtra("counterTimerValue", seconds)
            startService(intent)
        }

        this.bindCounterService()
    }

    private fun stopService() {
        Intent(this, CounterService::class.java).also { intent ->
            intent.action = "GESPO_COUNTER_SERVICE"
            stopService(intent)
        }
    }

    private fun configureNumberPickers() {
        // Config Hours
        np_hours.minValue = 0
        np_hours.maxValue = 23

        np_hours.setFormatter { value: Int ->
            return@setFormatter String.format("%02d", value)
        }

        // Config Minutes
        np_minutes.minValue = 0
        np_minutes.maxValue = 59

        np_minutes.setFormatter { value: Int ->
            return@setFormatter String.format("%02d", value)
        }

        // Config Seconds
        np_seconds.minValue = 0
        np_seconds.maxValue = 59

        np_seconds.setFormatter { value: Int ->
            return@setFormatter String.format("%02d", value)
        }

    }

    private fun registerBroadcastNotify() {
        this.counterNotifyBroadcastReceiver = CounterNotifyBroadcastReceiver()
        val intentFilterNotify = IntentFilter()

        intentFilterNotify.addAction("GESPO_COUNTER_NOTIFY")
        intentFilterNotify.addCategory(Intent.CATEGORY_DEFAULT)

        registerReceiver(this.counterNotifyBroadcastReceiver, intentFilterNotify)
    }

    private fun registerBroadcastFront() {
        this.counterBroadcastReceiver = CounterBroadcastReceiver()
        val intentFilterFrontUpdate = IntentFilter()

        intentFilterFrontUpdate.addAction("GESPO_COUNTER_FRONT")
        intentFilterFrontUpdate.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(this.counterBroadcastReceiver, intentFilterFrontUpdate)
    }

    private fun bindCounterService() {
        Intent(this, CounterService::class.java).also { intent ->
            bindService(intent, serviceConnection, 0)
        }
    }

    private fun convertSecondsToStringTimerAndPutInView(seconds: Int) {

        val secondsFinal: Int = seconds % 60

        val minutesDiff: Int = truncate((seconds / 60).toDouble()).toInt()
        val minutesFinal: Int = minutesDiff % 60

        val hoursDiff: Int = truncate((minutesDiff / 60).toDouble()).toInt()
        val hoursFinal: Int = hoursDiff

        np_hours.value = hoursFinal
        np_minutes.value = minutesFinal
        np_seconds.value = secondsFinal

        tv_hours.text = String.format("%02d", hoursFinal)
        tv_minutes.text = String.format("%02d", minutesFinal)
        tv_seconds.text = String.format("%02d", secondsFinal)
    }

    private fun getSecondsOnView(): Int {
        return (np_seconds.value + (np_minutes.value * 60) + (np_hours.value * 3600))
    }

    private fun setEnableScrollNumberPickers(enable: Boolean) {

        this.convertSecondsToStringTimerAndPutInView(
            this.getSecondsOnView()
        )

        np_hours.isEnabled = enable
        np_minutes.isEnabled = enable
        np_seconds.isEnabled = enable

        setViewVisibility(enable)
    }

    private fun setViewVisibility(showViews: Boolean) {
        if (showViews) {
            np_hours.visibility = View.VISIBLE
            np_minutes.visibility = View.VISIBLE
            np_seconds.visibility = View.VISIBLE

            cl_display_numbers.visibility = View.GONE
        } else {
            np_hours.visibility = View.INVISIBLE
            np_minutes.visibility = View.INVISIBLE
            np_seconds.visibility = View.INVISIBLE

            cl_display_numbers.visibility = View.VISIBLE
        }
    }

    fun updateTextViewCounter() {

        val secondsThread: Int = this.CounterSvcController.getCounterTimer()

        this.convertSecondsToStringTimerAndPutInView(secondsThread)

    }

}
