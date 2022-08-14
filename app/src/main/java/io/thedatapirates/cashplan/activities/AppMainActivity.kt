package io.thedatapirates.cashplan.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.domains.cashflow.CashFlowFragment
import io.thedatapirates.cashplan.domains.expense.ExpenseFragment
import io.thedatapirates.cashplan.domains.helpcenter.HelpCenterFragment
import io.thedatapirates.cashplan.domains.home.HomeFragment
import io.thedatapirates.cashplan.domains.investment.InvestmentFragment
import io.thedatapirates.cashplan.domains.profile.ProfileFragment
import io.thedatapirates.cashplan.domains.setting.SettingFragment
import kotlinx.android.synthetic.main.activity_app_main.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class AppMainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    /**
     * Handles adding listners to switch between fragments when any navigation button is selected
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_main)

        val homeFragment = HomeFragment()
        val expenseFragment = ExpenseFragment()
        val cashFlowFragment = CashFlowFragment()
        val investmentFragment = InvestmentFragment()
        val profileFragment = ProfileFragment()
        val settingFragment = SettingFragment()
        val helpCenterFragment = HelpCenterFragment()

        var current: Fragment = homeFragment

        addFragment(current)

        bottomNav = findViewById(R.id.navBottomNavigation)
        navView = findViewById(R.id.nvTopNavigationWithHeader)
        drawerLayout = findViewById(R.id.dlMainAppActivity)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        val headerView = navView.getHeaderView(0)
        val customerNameView = headerView.findViewById<TextView>(R.id.tvCustomerName)
        val customerEmailView = headerView.findViewById<TextView>(R.id.tvCustomerEmail)
        val customerName = "${
            sharedPreferences.getString(
                "customerFirstName",
                ""
            )
        } ${sharedPreferences.getString("customerLastName", "")}"
        customerNameView.text = customerName
        customerEmailView.text = sharedPreferences.getString("userEmail", "")

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when (it.itemId) {
                R.id.navProfileFragment -> {
                    if (current !is ProfileFragment) {
                        // makes sure bottom nav is not selected
                        bottomNav.selectedItemId = R.id.navInvisible
                        removeFragment(current)
                        current = profileFragment
                        addFragment(current)
                        drawerLayout.close()
                    }
                }
                R.id.navSettingFragment -> {
                    if (current !is SettingFragment) {
                        // makes sure bottom nav is not selected
                        bottomNav.selectedItemId = R.id.navInvisible
                        removeFragment(current)
                        current = settingFragment
                        addFragment(current)
                        drawerLayout.close()
                    }
                }
                R.id.navLogOut -> {
                    val editSettings =
                        this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit()

                    // removes all settings related to getting customer information in api
                    editSettings.remove("accessToken")
                    editSettings.remove("refresherToken")
                    editSettings.remove("userEmail")
                    editSettings.apply()

                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.navHelpCenterFragment -> {
                    if (current !is HelpCenterFragment) {
                        // makes sure bottom nav is not selected
                        bottomNav.selectedItemId = R.id.navInvisible
                        removeFragment(current)
                        current = helpCenterFragment
                        addFragment(current)
                        drawerLayout.close()
                    }
                }
            }
            true
        }

        navBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHomeFragment -> {
                    if (current !is HomeFragment) {
                        deselectDrawerItems()
                        removeFragment(current)
                        current = homeFragment
                        addFragment(current)
                    }
                }
                R.id.navExpenseFragment -> {
                    if (current !is ExpenseFragment) {
                        deselectDrawerItems()
                        removeFragment(current)
                        current = expenseFragment
                        addFragment(current)
                    }
                }
                R.id.navCashFlowFragment -> {
                    if (current !is CashFlowFragment) {
                        deselectDrawerItems()
                        removeFragment(current)
                        current = cashFlowFragment
                        addFragment(current)
                    }
                }
                R.id.navInvestmentFragment -> {
                    if (current !is InvestmentFragment) {
                        deselectDrawerItems()
                        removeFragment(current)
                        current = investmentFragment
                        addFragment(current)
                    }
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Deselects all drawer items
     */
    private fun deselectDrawerItems() {
        val navMenu = navView.menu

        navMenu.findItem(R.id.navProfileFragment).isChecked = false
        navMenu.findItem(R.id.navHelpCenterFragment).isChecked = false
        navMenu.findItem(R.id.navSettingFragment).isChecked = false
    }

    /**
     * Adds selected fragment into view
     */
    private fun addFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, fragment)
            commit()
        }

    /**
     * Removes selected fragment from view
     */
    private fun removeFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            remove(fragment)
            commit()
        }
}