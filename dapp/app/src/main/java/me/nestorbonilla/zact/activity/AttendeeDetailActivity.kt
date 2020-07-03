package me.nestorbonilla.zact.activity

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
import me.nestorbonilla.zact.R

class AttendeeDetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendee_detail)

        val toolbar:Toolbar = findViewById(R.id.attendee_detail_toolbar)
        setSupportActionBar(toolbar)

        val attendee_detail_image:SimpleDraweeView = findViewById(R.id.attendee_detail_image)
        attendee_detail_image.setImageURI("https://maps.googleapis.com/maps/api/staticmap?center=8.994772,-79.522794&zoom=13&size=600x300&maptype=roadmap&key=AIzaSyAdet-b7ik-7xdThfIS_GhPShjavJSUz18")

        //val collapsingToolbar:CollapsingToolbarLayout = findViewById(R.id.dashboard_detail_collapser)
        //collapsingToolbar.title = "This is the title"
    }

}