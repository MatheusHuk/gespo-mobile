package br.com.bandtec.gespo.services.broadcasts

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.bandtec.gespo.CounterActivity
import br.com.bandtec.gespo.R


class CounterNotifyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("GESPO_COUNTER_NOTIFY", "Chamado recebido (NOTIFICACAO)")

        showNotification(context);
    }

    fun showNotification(context: Context?) {
        val intent: Intent = Intent(context, CounterActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notificationCompat = NotificationCompat.Builder(context)
                                                   .setSmallIcon(R.drawable.ic_alarm)
                                                   .setContentTitle("Tempo Esgotado!")
                                                   .setContentText("Aponte suas horas :)")
                                                   .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationCompat.setContentIntent(contentIntent)
        notificationCompat.setDefaults(Notification.DEFAULT_SOUND)
        notificationCompat.setAutoCancel(true)

        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, notificationCompat.build())
    }

}