package me.nestorbonilla.zact.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.Twig
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_creator_detail.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.CreatorModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.service.ServiceBuilder
import me.nestorbonilla.zact.service.ZactService
import me.nestorbonilla.zact.utility.SampleStorageBridge
import me.nestorbonilla.zact.utility.SimpleMnemonics
import okio.internal.commonToUtf8String
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class CreatorDetailActivity: AppCompatActivity() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null
    private lateinit var actModel: ActModel
    private lateinit var creatorModel: CreatorModel

    private val initializer: Initializer = Initializer(App.instance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creator_detail)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        // verify if is a new or a previous act
        var actId = intent.getIntExtra("act_id", 0)

        if (actId > 0) {
            loadValues(actId)
            creator_map_button.isEnabled = true
            creator_map_button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            creator_private_button.isEnabled = true
            creator_private_button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        } else {
            loadDefaultValues()
        }

        creator_detail_button.setOnClickListener({
            Observable.fromCallable(
                {
                    saveAct()
                    db?.zactDao()?.getActList()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        })

        creator_map_button.setOnClickListener {
            val intent = Intent(this, CreatorMapActivity::class.java)
            intent.putExtra("act_id", actModel.id)
            intent.putExtra("act_radius", actModel.meetingPointRadius)
            intent.putExtra("act_coordinatee", actModel.meetingPoint)
            this.startActivity(intent)
        }

        creator_private_button.setOnClickListener {
            val intent = Intent(this, CreatorPrivateActivity::class.java)
            intent.putExtra("act_id", actModel.id)
            this.startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    private fun saveAct() {

        //1. Generate new seed phrase
        val fullSeedPhrase = Mnemonics.MnemonicCode(Mnemonics.WordCount.COUNT_24).joinToString().replace(",", "")
        val actAddress = initializer.deriveAddress(SimpleMnemonics().toSeed(fullSeedPhrase.toCharArray()))
        val preSeedPhrase = fullSeedPhrase.split(" ").slice(0..21).toString().replace(",", "").replace("[", "").replace("]", "")
        val missingWords = fullSeedPhrase.split(" ").slice(22..23).toString().replace(",", "").replace("[", "").replace("]", "")

        //2. Create ActModel object
        var act = ActModel(
            0,
            "",
            creatorModel.address,
            actAddress,
            preSeedPhrase,
            creator_title.text.toString(),
            creator_public.text.toString(),
            0,
            ""
        )

        //3. Send it to the API
        val zactService = ServiceBuilder.buildService(ZactService::class.java)
        val requestCall = zactService.addAct(act)

        requestCall.enqueue(object: Callback<ActModel> {

            override fun onResponse(call: Call<ActModel>, response: Response<ActModel>) {
                if (response.isSuccessful) {
                    //Log.d("ZACT_APP", response.body().toString())
                    var newAct = response.body() // Use it or ignore it
                    if (!newAct!!._id.isEmpty()) {

                        // Updating UI
                        creator_missing.setText(missingWords)
                        creator_map_button.isEnabled = true
                        creator_map_button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                        creator_private_button.isEnabled = true
                        creator_private_button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

                        act._id = newAct._id
                        act.seed = fullSeedPhrase // full seed phrase is stored only in the device
                        act.actAddress = actAddress
                        with(zactDao) {
                            this?.insertAct(act)
                            actModel = this?.getLastAct()!!

                            // adding 1 to the record of acts created
                            creatorModel.actsCreated++
                            this?.updateCreator(creatorModel)
                        }
                    }
                } else {
                    //Log.d("ZACT_APP", response.body().toString())
                }
            }
            override fun onFailure(call: Call<ActModel>, t: Throwable) {

            }
        })
    }

    private fun loadValues(actId: Int) {
        with(zactDao) {
            creatorModel = this?.getCreator(1)!!
            actModel = this?.getAct(actId)!!
        }
        Log.d("ZACT_DAPP", creatorModel.address)
        creator_from.setText(creatorModel.address)
        creator_title.setText(actModel.title)
        creator_public.setText(actModel.publicInformation)
        creator_missing.setText(actModel.seed.split(" ").slice(22..23).toString().replace(",", "").replace("[", "").replace("]", ""))
    }

    private fun loadDefaultValues() {
        with(zactDao) {
            creatorModel = this?.getCreator(1)!!
        }
        creator_from.setText(creatorModel.address)
    }

}