package io.thedatapirates.cashplan.domains.login

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.domains.settings.StNotifications

class LoginActivity : AppCompatActivity() {
    private val stNotif: StNotifications = StNotifications()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        reminderNotifChnl()
        alarmNotifChnl()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ca\$hPlanReminderChannel"
            val description = "Channel for reminding customers about expenses that need to be paid"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("REMINDERS_CHANNEL", name, importance)

            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Notification Categories
     * reminder notification
     */

    private fun reminderNotifChnl(){
        // check if device is running android 8.0 or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //creates notification item in the device settings
            val name = "Reminders"
            val Description = "Any reminders set will be placed here"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.stNotifChnl_id01), name, importance).apply {
                enableVibration(stNotif.vibrate)
                description = Description
                group = getString(R.string.stNotifCatID)
            }

            // Register the channel with the users device
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(getString(R.string.stNotifCatID), getString(R.string.reminders)))
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun alarmNotifChnl(){
        // check if device is running android 8.0 or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //creates notification item in the device settings
            val name = "Alarms"
            val Description = "Any alarms set will be placed here"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.stNotifChnl_id02), name, importance).apply {
                enableVibration(stNotif.vibrate)
                description = Description
                group = getString(R.string.stNotifCatID)
            }

            // Register the channel with the users device
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannelGroup(NotificationChannelGroup(getString(R.string.stNotifCatID), getString(R.string.reminders)))
            notificationManager.createNotificationChannel(channel)
        }


    }
}