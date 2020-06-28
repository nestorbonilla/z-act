package me.nestorbonilla.zact.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard_create.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.viewmodel.ActViewModel

class DashboardCreateActivity: AppCompatActivity() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

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
        })
    }
}