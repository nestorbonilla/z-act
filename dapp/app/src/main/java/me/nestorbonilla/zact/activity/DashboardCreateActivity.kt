package me.nestorbonilla.zact.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.Twig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dashboard_create.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.utility.SampleStorageBridge
import androidx.lifecycle.lifecycleScope
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.ext.twig
import android.view.LayoutInflater
import cash.z.ecc.android.sdk.ext.collectWith

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

        startSynchronizer()

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

    private fun startSynchronizer() {
        lifecycleScope.apply {
            synchronizer.start(this)
        }
    }

    /*private fun onSend(unused: View) {
        isSending = true
        val amount = amountInput.text.toString().toDouble().convertZecToZatoshi()
        val toAddress = addressInput.text.toString().trim()
        synchronizer.sendToAddress(
            keyManager.key,
            amount,
            toAddress,
            "Demo App Funds"
        ).collectWith(lifecycleScope, ::onPendingTxUpdated)
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
        binding.textInfo.apply {
            text = "$text\n$message"
        }
    }

    private fun onResetInfo() {
        binding.textInfo.text = "Active Transaction:"
    }*/
}