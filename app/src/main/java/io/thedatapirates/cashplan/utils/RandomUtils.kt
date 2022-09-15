package io.thedatapirates.cashplan.utils

import java.util.concurrent.atomic.AtomicInteger

object RandomUtils {
    private val seed = AtomicInteger()
    const val ACTION_SET_ALARM = "ACTION_SET_ALARM"
    const val ACTION_SET_REMINDER = "ACTION_SET_REMINDER"
    const val EXTRA_ALARM_TIME = "EXTRA_ALARM_TIME"

    fun getRndInt():Int = seed.getAndIncrement()+System.currentTimeMillis().toInt()


}