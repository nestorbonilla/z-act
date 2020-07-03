package me.nestorbonilla.zact.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.block.CompactBlockProcessor
import cash.z.ecc.android.sdk.ext.Twig
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_creator_detail.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.utility.SampleStorageBridge

class CreatorDetailActivity: AppCompatActivity() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

    private lateinit var creator_from: TextInputEditText
    private lateinit var creator_title: TextInputEditText
    private lateinit var creator_public: TextInputEditText
    private lateinit var creator_radius: TextInputEditText
    private lateinit var creator_map_button: AppCompatButton
    private lateinit var creator_private_button: AppCompatButton
    private lateinit var creator_seed: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creator_detail)

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        creator_detail_button.setOnClickListener({
            Observable.fromCallable(
                {
                    db = ZactDatabase.getDatabase(this)
                    zactDao = db?.zactDao()
                    var act = ActModel(
                        0,
                        creator_from.text.toString(),
                        creator_title.text.toString(),
                        creator_public.text.toString(),
                        creator_radius.text.toString().toInt(),
                        creator_seed
                    )
                    with(zactDao) {
                        this?.insertAct(act)
                    }
                    db?.zactDao()?.getActList()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
            this.finish()
        })
    }
/*
    private fun startSynchronizer() {
        lifecycleScope.apply {
            synchronizer.start(this)
        }
    }

    private fun onSend(unused: View) {
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