package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.thedatapirates.cashplan.R

/**
 * A simple [Fragment] subclass.
 * key is at bottom of the file
 */
class StAccount : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_st_account, container, false)
        //update the text here...an edit feature will be given for the personal info but thats
        //for a later time
//        val FName: TextView = view.findViewById(R.id.StAccntTVI_FName)
//        val LName: TextView = view.findViewById(R.id.StAccntTVI_LName)
//        //first app start up leave blank then when given input send both verification and save val
//        val Phone: TextView =  view.findViewById(R.id.StAccntTVI_Phone)
//        val email: TextView =  view.findViewById(R.id.StAccntTVI_Email)
//        //if check here to see if user has bank account listed
//        //for settings visibility 0:Vis 1:InVis space left 2:InVis removes space
//        val Bank1: TextView =  view.findViewById(R.id.StAccntTV_Bank1Name)
//        //next if for checking accounts
//        val B1C1Name: TextView =  view.findViewById(R.id.StAccntTV_B1C1Name)
//        val B1C1Number: TextView =  view.findViewById(R.id.StAccntTV_B1C1Number)
//        //next if for savings account
//        val SavingsName1: TextView = view.findViewById(R.id.StAccntTV_B1S1Name)
//        val B1S1Number: TextView = view.findViewById(R.id.StAccntTV_B1S1Number)
////        SavingsName1.text = "new text is given"
        return view
    }

}