package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_st_notifications.view.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class StNotifications : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_st_notifications, container, false)
//        view.stNotifTv_AlertCustom.setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.stNotif_to_stMenu)
//        }

        return view
    }
}