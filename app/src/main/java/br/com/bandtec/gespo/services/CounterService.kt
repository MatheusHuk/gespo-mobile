package br.com.bandtec.gespo.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import br.com.bandtec.gespo.services.listeners.CounterListener

class CounterService() : Service(), CounterListener {

    private var isActive: Boolean = false
    private var counterTimer = 0
    private val counterSvcController: CounterSvcController = CounterSvcController()
    private var thread: Thread? = null

    inner class CounterSvcController : Binder() {
        fun getCountListener(): CounterListener = this@CounterService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return this.counterSvcController
    }

    override fun onCreate() {

        Log.i("GESPO_COUNTER_SERVICE", "onCreate()")

        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("GESPO_COUNTER_SERVICE", "onStartCommand()")

        if (!this.isActive) {
            this.counterTimer = intent!!.getIntExtra("counterTimerValue", 0)
            this.startThread()
        }
        return START_STICKY
    }

    override fun onDestroy() {

        Log.i("GESPO_COUNTER_SERVICE", "onDestroy()")

        super.onDestroy()

        if(this.thread != null) {
            this.thread!!.interrupt()
            this.isActive = false
        }

    }

    fun startThread() {

        val intentCounterFront: Intent = Intent("GESPO_COUNTER_FRONT")

        this.isActive = true

        Log.i("StartingThread", "CounterTimer: ${this.counterTimer}, IsActive: ${this.isActive}")

        this.thread = Thread() {

            while (this.isActive && this.counterTimer > 0) {

                try {
                    Thread.sleep(1000)

                } catch (exception: InterruptedException) {
                    println(exception.message)
                }

                this.counterTimer--

                sendBroadcast(intentCounterFront)

                Log.i("Thread", "CounterTimer: ${this.counterTimer}")
            }

            if(this.counterTimer == 0) {
                val intentNotify: Intent = Intent("GESPO_COUNTER_NOTIFY")
                sendBroadcast(intentNotify)
            }

            this.isActive = false

        }

        this.thread!!.start()

    }

    override fun getCounterTimer(): Int {
        return this.counterTimer
    }

    override fun setCounterTimer(counterTimer: Int) {
        this.counterTimer = counterTimer
    }

}