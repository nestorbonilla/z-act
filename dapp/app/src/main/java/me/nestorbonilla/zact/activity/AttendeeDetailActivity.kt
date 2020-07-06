package me.nestorbonilla.zact.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagedList
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.db.entity.ConfirmedTransaction
import cash.z.ecc.android.sdk.ext.collectWith
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_attendee_detail.*
import kotlinx.android.synthetic.main.activity_creator_detail.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import java.nio.charset.StandardCharsets

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
        loadValues()

        val key: String = applicationContext.getString(R.string.google_maps_key)
        var imageURI = "https://maps.googleapis.com/maps/api/staticmap?center=" + actModel.meetingPoint + "&zoom=13&size=600x300&maptype=roadmap&key=" + key
        val attendee_detail_image:SimpleDraweeView = findViewById(R.id.attendee_detail_image)
        attendee_detail_image.setImageURI(imageURI)

        attendee_map_button.setOnClickListener {
            val intent = Intent(this, AttendeeMapActivity::class.java)
            intent.putExtra("act_id", actModel.id)
            this.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadValues()
    }

    private fun loadValues() {
        with(zactDao) {
            actModel = this?.getAct(actId)!!
        }
        attendee_detail_title.setText(actModel.title)
        attendee_detail_public.setText(actModel.publicInformation)
        if (actModel.seed.split(" ").size == 24) {
            attendee_detail_private_card.isVisible = true
            //App.instance.onCreateWallet(actModel.seed)
            //App.instance.isCreated = true
            App.instance.onCreateWallet(actModel.seed)
            App.instance.synchronizer.status.collectWith(App.instance.appScope, ::onStatusUpdate)
            App.instance.synchronizer.clearedTransactions.collectWith(App.instance.appScope, ::onStatusTransaction)
        }
    }

    private fun onStatusUpdate(status: Synchronizer.Status) {
        attendee_detail_private_title.text = "Secret Information: " + status.name
    }

    private fun onStatusTransaction(pagedList: PagedList<ConfirmedTransaction>) {
        val lastTransaction = pagedList.lastOrNull()
        Log.d("@TWIG", "memo = ${lastTransaction?.memo.toUtf8Memo()}")
        attendee_detail_private.setText(lastTransaction?.memo.toUtf8Memo())
    }

    inline fun ByteArray?.toUtf8Memo(): String {
        // TODO: make this more official but for now, this will do
        return if (this == null || this[0] >= 0xF5) "" else try {
            String(this, StandardCharsets.UTF_8).trim('\u0000')
        } catch (t: Throwable) {
            "unable to parse memo"
        }
    }
}