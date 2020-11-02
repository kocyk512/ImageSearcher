package com.krzysztofkocot.imagesearcher.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.jakewharton.rxbinding4.view.clicks
import com.krzysztofkocot.imagesearcher.ENABLE_BLUETOOTH_REQUEST_CODE
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.ui.dialog.showDevicesDialog
import com.krzysztofkocot.imagesearcher.ui.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.buttonMain


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val items = arrayOf("aaa", "bbbbb", "ccccc")
        buttonMain.clicks()
            .subscribe {
                showDevicesDialog(
                    this, items,
                    {
                        toast(this, items[it])
                    },
                    {
                        toast(this, items[it - 1])

                    }
                )
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}