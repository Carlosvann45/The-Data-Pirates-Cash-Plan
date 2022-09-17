@file:OptIn(DelicateCoroutinesApi::class)

package io.thedatapirates.cashplan.domains.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.thedatapirates.cashplan.utils.RandomUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmService(private val context: Context) {

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
        manager.let {
            manager?.setExactAndAllowWhileIdle(
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
            PendingIntent.FLAG_IMMUTABLE
        )


}


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val timeInMillis = intent.getLongExtra(RandomUtils.EXTRA_ALARM_TIME, 0L)
//        val activity = activity as SettingsActivity

        when (intent.action) {
            RandomUtils.ACTION_SET_ALARM -> {
//              activity.sendNotification()
            }
            RandomUtils.ACTION_SET_REMINDER ->{
                val cal = Calendar.getInstance().apply {
                    this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)
                }
                AlarmService(context).setRemind(cal.timeInMillis)

//                createNotif(context, "Reminding you", convertDate(cal.timeInMillis), 102)
            }
        }
    }


    private fun convertDate(timeinMillis: Long): String =
        android.text.format.DateFormat.format("MM/dd/yyyy hh:mm:ss", timeinMillis) as String
}