package me.nestorbonilla.zact.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.drawee.backends.pipeline.Fresco
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.room.ZactDatabase

class HomeActivity : AppCompatActivity() {

    lateinit var preference: SharedPreferences
    val pref_show_intro = "INTRO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialization
        Fresco.initialize(this)
        preference = getSharedPreferences("INTRO_SLIDER", Context.MODE_PRIVATE)

        if(preference.getBoolean(pref_show_intro, true)) {
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_attendee,
                R.id.navigation_creator,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}