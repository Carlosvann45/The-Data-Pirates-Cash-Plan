package io.thedatapirates.cashplan.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

/**
 * Class for global misc helper functions
 */
class AndroidUtils {
    companion object {
        /**
         * Handles creating the custom toast message
         */
        fun createCustomToast(message: String, existingView: View, newContext: Context?): Toast {
            val newToast = Toast(newContext)

            newToast.apply {
                val toastLayout =
                    (existingView as ViewGroup).inflate(newContext, R.layout.custom_toast)

                toastLayout.tvToastMessage.text = message
                setGravity(Gravity.TOP, 0, 0)
                duration = Toast.LENGTH_LONG
                view = toastLayout
            }

            return newToast
        }

        /**
         * Inflates the toast message int view
         */
        private fun ViewGroup.inflate(
            context: Context?,
            @LayoutRes layoutRes: Int,
            attachToRoot: Boolean = false
        ): View {
            return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
        }

        /**
         * Animates a view to fade in and fade out
         */
        fun animateView(view: View, toVisibility: Int, toAlpha: Float, duration: Long) {
            val show = toVisibility == view.visibility

            if (show) view.alpha = 0f

            view.visibility = view.visibility
            view.animate()
                .setDuration(duration)
                .alpha(if (show) toAlpha else 0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animator: Animator) {
                        view.visibility = toVisibility
                    }
                })
        }

        fun getMonthName() : String {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("MMMM", Locale.getDefault())

            return formatter.format(time)
        }

        /**
         * Checks if date month is equal to current month
         */
        fun compareCurrentMonth(date: String) : Boolean {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            val dateNow = formatter.format(time).substring(6, 7)
            val newDate = date.substring(6, 7)

            return dateNow.compareTo(newDate) == 0
        }

        /**
         * Validates start and end dates
         */
        fun compareDates(startDate: String?, endDate: String?) : Boolean {
            val dateValid: Boolean
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            val dateNow = formatter.format(time)

            dateValid = if (!startDate.isNullOrBlank() && !endDate.isNullOrBlank()) {
                val cmpStartEnd = startDate.compareTo(endDate)
                val cmpStartNow = startDate.compareTo(dateNow)

                cmpStartEnd <= 0 && cmpStartNow >= 0

            } else if (!startDate.isNullOrBlank()) {
                val cmpStartNow = startDate.compareTo(dateNow)

                cmpStartNow <= 0
            } else false

            return dateValid
        }

    }
}