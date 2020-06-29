package me.nestorbonilla.zact.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.Twig
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard_create.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.utility.SampleStorageBridge
import me.nestorbonilla.zact.viewmodel.ActViewModel

class DashboardCreateActivity: AppCompatActivity() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

    private val config = App.instance.defaultConfig
    private val initializer = Initializer(App.instance, host = config.host, port = config.port)
    private val birthday = config.loadBirthday()

    private lateinit var synchronizer: Synchronizer
    private lateinit var keyManager: SampleStorageBridge

    private lateinit var amountInput: TextView
    private lateinit var addressInput: TextView

    //
    // Observable properties (done without livedata or flows for simplicity)
    //

    private var balance = CompactBlockProcessor.WalletBalance()
    set(value) {
        field = value
    }
    private var isSending = false
    set(value) {
        field = value
        if (value) Twig.sprout("Sending") else Twig.clip("Sending")
    }
    private var isSyncing = true
    set(value) {
        field = value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_create)

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        dashboard_create_button.setOnClickListener({
            Observable.fromCallable(
                {
                    db = ZactDatabase.getDatabase(this)
                    zactDao = db?.zactDao()
                    var act = ActModel(0, "event 3", "description 3 ")
                    with(zactDao) {
                        this?.insertAct(act)
                    }
                    db?.zactDao()?.getActList()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            this.finish()
        })
    }
}