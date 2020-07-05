package me.nestorbonilla.zact.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.sdk.Initializer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_creator_private.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.service.ServiceBuilder
import me.nestorbonilla.zact.service.ZactService
import me.nestorbonilla.zact.utility.SimpleMnemonics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets

class CreatorPrivateActivity : AppCompatActivity() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null
    private var isNew = true
    private lateinit var fromAddress: String
    private lateinit var actModel: ActModel

    private val initializer: Initializer = Initializer(App.instance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creator_private)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()

        fromAddress = initializer.deriveAddress(App.instance.config.seed)
        creator_private_from.setText(fromAddress)

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        // verify if is a new or a previous act
        var actId = intent.getIntExtra("act_id", 0)

        if (actId > 0) {
            isNew = false
            loadValues(actId)
            //loadValues(actId)
            //creator_map_button.isEnabled = true
            //creator_map_button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            //creator_private_button.isEnabled = true
            //creator_private_button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        }

        creator_private_send_button.setOnClickListener({
            Observable.fromCallable(
                {
                    db?.zactDao()?.getActList()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            //this.finish()
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }


    inline fun ByteArray?.toUtf8Memo(): String {
        // TODO: make this more official but for now, this will do
        return if (this == null || this[0] >= 0xF5) "" else try {
            String(this, StandardCharsets.UTF_8).trim('\u0000')
        } catch (t: Throwable) {
            "unable to parse memo"
        }
    }

    private fun loadValues(actId: Int) {
        with(zactDao) {
            actModel = this?.getAct(actId)!!
        }
        creator_private_to.setText(actModel.actAddress)
        //creator_title.setText(actModel.title)
        //creator_public.setText(actModel.publicInformation)
        //creator_missing.setText(actModel.actAddress)
        //creator_missing.setText(actModel.seed.split(" ").slice(22..23).toString().replace(",", "").replace("[", "").replace("]", ""))
        //creator_missing.setText(actModel.seed)
    }

}