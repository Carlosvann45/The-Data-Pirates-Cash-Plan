package io.thedatapirates.cashplan.domains.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.DelicateCoroutinesApi


/**
 * A simple [Fragment] subclass.
 */
@DelicateCoroutinesApi
class HomeFragment : Fragment() {

    private lateinit var homeContext: Context

    /**
     * Runs listener's when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sharedPreferences =
            homeContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val firstName = sharedPreferences.getString("customerFirstName", "")
        val lastName = sharedPreferences.getString("customerLastName", "")

        view.tvHomeText.text =
            getString(
                R.string.home_welcome,
                firstName,
                lastName
            )

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContext = context
    }

}