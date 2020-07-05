package me.nestorbonilla.zact.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_attendee_detail.*
import kotlinx.android.synthetic.main.activity_creator_detail.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase

class AttendeeDetailActivity: AppCompatActivity() {

    private var actId: Int = 0
    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null
    private lateinit var actModel: ActModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()

        setContentView(R.layout.activity_attendee_detail)
        val toolbar:Toolbar = findViewById(R.id.attendee_detail_toolbar)
        setSupportActionBar(toolbar)

        actId = intent.getIntExtra("act_id", 0)

        val attendee_detail_image:SimpleDraweeView = findViewById(R.id.attendee_detail_image)
        attendee_detail_image.setImageURI("https://maps.googleapis.com/maps/api/staticmap?center=8.994772,-79.522794&zoom=13&size=600x300&maptype=roadmap&key=AIzaSyAdet-b7ik-7xdThfIS_GhPShjavJSUz18")

        //val collapsingToolbar:CollapsingToolbarLayout = findViewById(R.id.dashboard_detail_collapser)
        //collapsingToolbar.title = "This is the title"
        loadValues()

        attendee_map_button.setOnClickListener {
            val intent = Intent(this, AttendeeMapActivity::class.java)
            intent.putExtra("act_id", actModel.id)
            this.startActivity(intent)
        }
    }

    private fun loadValues() {
        with(zactDao) {
            actModel = this?.getAct(actId)!!
        }
        attendee_detail_public.setText(actModel.publicInformation)
    }
}