package br.com.bandtec.gespo.services.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import br.com.bandtec.gespo.CounterActivity

class CounterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i("GESPO_COUNTER_BROADCAST", "Chamado recebido (FRONT)")

        CounterActivity.getInstance()?.updateTextViewCounter()

    }

}