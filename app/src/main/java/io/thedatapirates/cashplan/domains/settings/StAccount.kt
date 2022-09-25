package io.thedatapirates.cashplan.domains.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.thedatapirates.cashplan.R
import kotlinx.coroutines.DelicateCoroutinesApi

/**
 * A simple [Fragment] subclass.
 * key is at bottom of the file
 */
@OptIn(DelicateCoroutinesApi::class)
class StAccount : Fragment() {
    lateinit var active: SettingsActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_st_account, container, false)
        //update the text here...an edit feature will be given for the personal info but thats
        //for a later time
        active = activity as SettingsActivity
        val sharedPreferences = active.getSharedPreferences("UserInfo",
            AppCompatActivity.MODE_PRIVATE
        )
        val FName: TextView = view.findViewById(R.id.StAccntTVI_FName)
        val LName: TextView = view.findViewById(R.id.StAccntTVI_LName)
        //first app start up leave blank then when given input send both verification and save val
        val email: TextView =  view.findViewById(R.id.StAccntTVI_Email)
        FName.text = sharedPreferences.getString("customerFirstName", "")
        LName.text = sharedPreferences.getString("customerLastName", "")
        email.text = sharedPreferences.getString("userEmail", "")
        return view
    }

}