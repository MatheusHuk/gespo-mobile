package br.com.bandtec.gespo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import br.com.bandtec.gespo.services.CounterService
import br.com.bandtec.gespo.services.broadcasts.CounterBroadcastReceiver
import br.com.bandtec.gespo.services.broadcasts.CounterNotifyBroadcastReceiver
import br.com.bandtec.gespo.services.listeners.CounterListener
import br.com.bandtec.gespo.utils.changeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_counter.*
import kotlin.math.truncate

class CounterActivity : AppCompatActivity(){

    private lateinit var CounterSvcController: CounterListener

    private var counterBroadcastReceiver: CounterBroadcastReceiver? = null
    private var counterNotifyBroadcastReceiver: CounterNotifyBroadcastReceiver? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as CounterService.CounterSvcController
            CounterSvcController = binder.getCountListener()
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

    var cookie:String = ""
    var name:String = ""
    var id:Int = 0

    var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cria a intância
        inst = this

        setContentView(R.layout.activity_counter)

        preferences = getSharedPreferences("Gespo", Context.MODE_PRIVATE)

        id = preferences?.getInt("id", 0)!!.toInt()
        name = preferences?.getString("username", "").toString()
        cookie = preferences?.getString("cookie", "").toString()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_timer

        //Esse é o código do spinner
        //Ele ta puxando lá do arquivo strings.xml as opções
        sp_options.adapter = ArrayAdapter(this,
        R.layout.support_simple_spinner_dropdown_item,
        resources.getStringArray(R.array.options))

        navView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                changeActivity(item ,this.applicationContext)
                return@OnNavigationItemSelectedListener true
            })

        // BIND!! Serve para termos o objeto CONTROLLER do serviço (E poder recuperar os valores)
        this.bindCounterService()

        // Criaçao do BroadcastReceiver que se mantém ouvindo para notificar o usuário
        this.registerBroadcastNotify()

        // Criar o Broadcast para mudar o Front
        this.registerBroadcastFront()

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

    fun startService () {

        Intent(this, CounterService::class.java).also { intent ->
            intent.action = "GESPO_COUNTER_SERVICE"
            intent.putExtra("counterTimerValue", 90)
            startService(intent)
        }

    }

   fun stopService () {

       Intent(this, CounterService::class.java).also { intent ->
           intent.action = "GESPO_COUNTER_SERVICE"
           stopService(intent)
       }

    }

    fun start (view: View) {
        /*
               Logica para INICIAR:
        */

        // 1 - Start Service:

        this.startService()

    }

    fun pause (view: View) {
        /*
               Logica para PAUSAR:
        */

        // STOP SERVICE
        this.stopService()

        // Usamos this.CounterSvcController.getCounterTimer()) para guardar de onde parou
        val currentTimer: Int = this.CounterSvcController.getCounterTimer()

        Log.i("CounterActivity", "CounterTimer: $currentTimer")

    }

    fun stop (view: View) {
        /*
               Logica para PARAR:
        */

        // STOP SERVICE
        this.stopService()

        // Setamos o texto para 0
        val currentTimer: Int = 0

        Log.i("CounterActivity", "CounterTimer: $currentTimer")

    }

    fun registerBroadcastNotify() {
        this.counterNotifyBroadcastReceiver = CounterNotifyBroadcastReceiver()
        val intentFilterNotify: IntentFilter = IntentFilter()

        intentFilterNotify.addAction("GESPO_COUNTER_NOTIFY")
        intentFilterNotify.addCategory(Intent.CATEGORY_DEFAULT)

        registerReceiver(this.counterNotifyBroadcastReceiver, intentFilterNotify)
    }

    fun registerBroadcastFront() {
        this.counterBroadcastReceiver = CounterBroadcastReceiver()
        val intentFilterFrontUpdate: IntentFilter = IntentFilter()

        intentFilterFrontUpdate.addAction("GESPO_COUNTER_FRONT")
        intentFilterFrontUpdate.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(this.counterBroadcastReceiver, intentFilterFrontUpdate)
    }

    fun bindCounterService() {
        Intent(this, CounterService::class.java).also { intent ->
            bindService(intent, serviceConnection, 0)
        }
    }

    fun convertSecondsToStringTimer(seconds: Int): String {

        var secondsFinal: Int = seconds % 60

        val minutesDiff: Int = truncate((seconds / 60).toDouble()).toInt()
        var minutesFinal: Int = minutesDiff % 60

        val hoursDiff: Int = truncate((minutesDiff / 60).toDouble()).toInt()
        var hoursFinal: Int = hoursDiff

        val secondsString: String = if(secondsFinal < 10) "0$secondsFinal" else secondsFinal.toString()
        val minutesString: String = if(minutesFinal < 10) "0$minutesFinal" else minutesFinal.toString()
        val hoursString: String = if(hoursFinal < 10) "0$hoursFinal" else hoursFinal.toString()

        return "$hoursString:$minutesString:$secondsString"
    }

    fun updateTextViewCounter() {

        val secondsThread: Int = this.CounterSvcController.getCounterTimer()

        tv_counter.text = this.convertSecondsToStringTimer(secondsThread)
    }

}
