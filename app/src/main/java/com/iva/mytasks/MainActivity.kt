package com.iva.mytasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))

        val navHost = supportFragmentManager
            .findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHost.navController

        // Само началният екран е без стрелка "назад"
        appBarConfiguration = AppBarConfiguration(setOf(R.id.taskListFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mode = when (item.itemId) {
            R.id.action_theme_light -> AppCompatDelegate.MODE_NIGHT_NO
            R.id.action_theme_dark -> AppCompatDelegate.MODE_NIGHT_YES
            R.id.action_theme_system -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> return super.onOptionsItemSelected(item)
        }
        getSharedPreferences(TaskApplication.PREFS, MODE_PRIVATE)
            .edit()
            .putInt(TaskApplication.KEY_NIGHT_MODE, mode)
            .apply()
        AppCompatDelegate.setDefaultNightMode(mode)
        return true
    }
}