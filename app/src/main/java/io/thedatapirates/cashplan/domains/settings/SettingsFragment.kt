package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class SettingsFragment : Fragment() {
    lateinit var settingsActivity: SettingsActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        view.StMB_Account.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.stMenu_to_stAccnt)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsActivity = activity as SettingsActivity
        StMB_Reminder.setOnClickListener{
            settingsActivity.openNotif(1)
        }
        StMB_Notify.setOnClickListener {
            settingsActivity.openNotif(0)
        }
        StMB_Alarms.setOnClickListener {
            settingsActivity.openNotif(2)
        }
    }
}


/*
//      ////    UNUSED CODE OR TEMP REMOVED    ////


*       ////    Name convention Key            ////
*       St              Settings
*       M               Menu
*       Accnt           Account
*       Btn / B         Button
*       TV              Text View
*       TVI             Text View designated for input items
*       ET              Edit text
*       #               notes which item it is in a list
*       B#              Bank Account linked
*       S#              Savings Account linked
*       C#              Checkings Account linked
*       Prsnl / Pnsl    Personal
*       Num             Number
*       Div             Divider (just a line on the screen)
*       FName           First Name
*       LName           Last Name
*/