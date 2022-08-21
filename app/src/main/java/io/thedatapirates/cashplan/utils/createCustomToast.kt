package io.thedatapirates.cashplan.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.custom_toast.view.*

/**
 * Class for displaying a custom toast message
 */
class CustomToast {
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
    }
}