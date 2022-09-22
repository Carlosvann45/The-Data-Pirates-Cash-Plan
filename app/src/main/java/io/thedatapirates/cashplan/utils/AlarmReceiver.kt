package io.thedatapirates.cashplan.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.data.dtos.reminder.ReminderResponse
import io.thedatapirates.cashplan.domains.login.LoginActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, LoginActivity::class.java)
        val id = intent.extras!!.getInt("id")
        val reminderJson = intent.extras!!.getString("reminder")
        val reminder = if (reminderJson != null)
            Gson().fromJson(
                reminderJson,
                object : TypeToken<ReminderResponse>() {}.type
            )
        else ReminderResponse()

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, id, i, 0)
        val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)

        mediaPlayer.isLooping = true
        mediaPlayer.start()

        val builder = NotificationCompat.Builder(context, context.getString(R.string.stNotifChnl_id02))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(reminder.name)
            .setContentText(reminder.description)
            .setAutoCancel(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(id, builder.build())
    }
}