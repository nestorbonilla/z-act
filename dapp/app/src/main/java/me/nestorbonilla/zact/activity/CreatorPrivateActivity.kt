package me.nestorbonilla.zact.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.db.entity.*
import cash.z.ecc.android.sdk.ext.collectWith
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.ext.twig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_creator_private.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.CreatorModel
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
    private lateinit var creatorModel: CreatorModel
    private var isSending = false

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
        loadValues(actId)

        creator_private_send_button.setOnClickListener({
            Observable.fromCallable(
                {
                    sendTransaction()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
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


    private fun sendTransaction() {
        isSending = true
        val amount = 0.00001.convertZecToZatoshi()
        val toAddress = actModel.actAddress
        val spendingKeys = getSpendingKey()
        App.instance.synchronizer?.sendToAddress(
            getSpendingKey(),
            amount,
            toAddress,
            creator_private_memo.text.toString()
        )?.collectWith(lifecycleScope, ::onPendingTxUpdated)
    }

    private fun getSpendingKey() : String {
        val seed = Mnemonics.MnemonicCode(creatorModel.seed).toSeed()
        return Initializer(this).deriveSpendingKeys(seed)[0]
    }

    private fun onPendingTxUpdated(pendingTransaction: PendingTransaction?) {
        val id = pendingTransaction?.id ?: -1
        val message = when {
            pendingTransaction == null -> "Transaction not found"
            pendingTransaction.isMined() -> "Transaction Mined (id: $id)!\n\nSEND COMPLETE".also { isSending = false }
            pendingTransaction.isSubmitSuccess() -> "Successfully submitted transaction!\nAwaiting confirmation..."
            pendingTransaction.isFailedEncoding() -> "ERROR: failed to encode transaction! (id: $id)".also { isSending = false }
            pendingTransaction.isFailedSubmit() -> "ERROR: failed to submit transaction! (id: $id)".also { isSending = false }
            pendingTransaction.isCreated() -> "Transaction creation complete! (id: $id)"
            pendingTransaction.isCreating() -> "Creating transaction!".also { onResetInfo() }
            else -> "Transaction updated!".also { twig("Unhandled TX state: $pendingTransaction") }
        }
        twig("Pending TX Updated: $message")
        creator_private_status.setText(message)
    }

    private fun onResetInfo() {
        creator_private_status.setText("Active Transaction:")
    }

}