package io.thedatapirates.cashplan.domains.cashflow

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.thedatapirates.cashplan.R
import io.thedatapirates.cashplan.domains.expense.ExpenseActivity
import io.thedatapirates.cashplan.domains.helpcenter.HelpCenterActivity
import io.thedatapirates.cashplan.domains.home.HomeActivity
import io.thedatapirates.cashplan.domains.investment.InvestmentActivity
import io.thedatapirates.cashplan.domains.login.LoginActivity
import io.thedatapirates.cashplan.domains.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_cash_flow.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CashFlowActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    /**
     * Handles adding listeners to switch between activities when any navigation button is selected
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_flow)

        bottomNav = findViewById(R.id.navCashFlowBottomNavigation)
        navView = findViewById(R.id.nvCashFlowTopNavigationWithHeader)
        drawerLayout = findViewById(R.id.dlCashFlowActivity)
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
                R.id.navSettingsActivity -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
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
                    overridePendingTransition(0, 0)
                }
                R.id.navHelpCenterActivity -> {
                    startActivity(Intent(this, HelpCenterActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.navWriteReview -> {
                    navView.menu.findItem(R.id.navWriteReview).isChecked = false

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=io.thedatapirates.cashplan")
                    )

                    startActivity(intent)
                }
                R.id.navFacebook -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://m.facebook.com/TheDataPirates")
                    )

                    navView.menu.findItem(R.id.navFacebook).isChecked = false

                    startActivity(intent)
                }
                R.id.navInstagram -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/data_pirates_cash_plan")
                    )

                    navView.menu.findItem(R.id.navInstagram).isChecked = false

                    startActivity(intent)
                }
                R.id.navLinkedIn -> {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.linkedin.com/company/cash-plan")
                    )

                    navView.menu.findItem(R.id.navLinkedIn).isChecked = false

                    startActivity(intent)
                }
            }
            true
        }

        navCashFlowBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHomeActivity -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.navExpenseActivity -> {
                    startActivity(Intent(this, ExpenseActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.navInvestmentActivity -> {
                    startActivity(Intent(this, InvestmentActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        bottomNav.selectedItemId = R.id.navCashFlowActivity
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}