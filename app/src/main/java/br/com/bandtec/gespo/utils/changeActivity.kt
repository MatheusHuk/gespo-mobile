package br.com.bandtec.gespo.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import br.com.bandtec.gespo.*

fun changeActivity(item:MenuItem, activity:Context){
    when(item.itemId){
        R.id.navigation_dashboard -> {
            val dashboardView = Intent(activity, MainActivity::class.java)
            dashboardView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(dashboardView)
        }
        R.id.navigation_timer -> {
            val timerView = Intent(activity, TimerActivity::class.java)
            timerView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(timerView)
        }
        R.id.navigation_apontamentos -> {
            val apontamentosView = Intent(activity, TimesheetConsultActivity::class.java)
            apontamentosView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(apontamentosView)
        }
        else -> {}
    }
}