package io.thedatapirates.cashplan.domains.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.thedatapirates.cashplan.utils.RandomUtils


class AlarmSettings(private val context: Context) {

    private val manager: AlarmManager? = context.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager?

    fun setAlarm(timeInMillis: Long) {
        createAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = RandomUtils.ACTION_SET_ALARM
                    putExtra(RandomUtils.EXTRA_ALARM_TIME, timeInMillis)
                }
            )
        )
    }

    fun setRemind(timeInMillis: Long) {
        createAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {
                    action = RandomUtils.ACTION_SET_REMINDER
                    putExtra(RandomUtils.EXTRA_ALARM_TIME, timeInMillis)
                }
            )
        )
    }

    private fun createAlarm(timeInMillis: Long, intent: PendingIntent) {
        manager?.let {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                intent
            )
        }
    }

    private fun getIntent(): Intent = Intent(context, AlarmReceiver::class.java)

    private fun getPendingIntent(intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            RandomUtils.getRndInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


}


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {


    }

}