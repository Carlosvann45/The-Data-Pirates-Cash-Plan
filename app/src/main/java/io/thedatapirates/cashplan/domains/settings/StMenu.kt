package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_st_menu.view.*
import io.thedatapirates.cashplan.R


/**
 * A simple [Fragment] subclass.
 */
class StMenu : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_st_menu, container, false)
        view.StMB_Account.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.stMenu_to_stAccnt)
        }
        return view
    }
}
/*
////    UNUSED CODE OR TEMP REMOVED ////

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }

*/