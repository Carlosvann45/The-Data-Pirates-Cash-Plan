package io.thedatapirates.cashplan.domains.login

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.thedatapirates.cashplan.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
}