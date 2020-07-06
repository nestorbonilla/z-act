package me.nestorbonilla.zact.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.drawee.backends.pipeline.Fresco
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.CreatorModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase

class HomeActivity : AppCompatActivity() {

    lateinit var preference: SharedPreferences
    val pref_show_intro = "INTRO"

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null
    private lateinit var creatorModel: CreatorModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()

        // Initialization
        Fresco.initialize(this)
        preference = getSharedPreferences("INTRO_SLIDER", Context.MODE_PRIVATE)

        if(preference.getBoolean(pref_show_intro, true)) {
            //Log.d("ZACT_DAPP", "before create initializer")
            //App.instance.createInitializer()
            //App.instance.onCreateWallet("")
            //App.instance.isCreated = true
            //Log.d("ZACT_DAPP", "after create initializer")
            startActivity(Intent(this, TutorialActivity::class.java))
        }

        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        with(zactDao) {
            creatorModel = this?.getCreator(1)!!
        }

        if (creatorModel.seed.isEmpty()) {
            navView.inflateMenu(R.menu.bottom_nav_attendee_menu)
            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.mobile_attendee_navigation)

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_attendee,
                    R.id.navigation_setting
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else {
            navView.inflateMenu(R.menu.bottom_nav_creator_menu)
            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.mobile_creator_navigation)

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_creator,
                    R.id.navigation_setting
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

    }
}