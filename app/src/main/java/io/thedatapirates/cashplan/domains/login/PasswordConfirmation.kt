package io.thedatapirates.cashplan.domains.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.thedatapirates.cashplan.R
import kotlinx.android.synthetic.main.fragment_password_confirmation.view.*

/**
 * A simple [Fragment] subclass.
 */
class PasswordConfirmation : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_password_confirmation, container, false)

        view.btnPasswordConfirmationFinish.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToLoginFragmentSlide)
        }

        view.bCashFlow.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateToCashFlowFragment) }

        return view
    }
}