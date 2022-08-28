package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.thedatapirates.cashplan.R
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_settings.view.*


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        view.StMB_Account.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.stMenu_to_stAccnt)
        }
        return view
    }
}
/*
//      ////    UNUSED CODE OR TEMP REMOVED    ////

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
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
