package me.nestorbonilla.zact.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.db.entity.ConfirmedTransaction
import cash.z.ecc.android.sdk.ext.collectWith
import kotlinx.android.synthetic.main.seed_dialog.view.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import java.nio.charset.StandardCharsets

class SettingFragment : Fragment() {

    private lateinit var address: String
    private lateinit var profile_address: TextView
    private var seedPhrase: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        profile_address = root.findViewById(R.id.profile_address)
        //profile_address.text = address

        //val profile_seed_phrase: TextInputLayout = root.findViewById(R.id.profile_seed_phrase)

        //val profile_seed_phrase: String = ""

        val profile_button: AppCompatButton = root.findViewById(R.id.profile_button)
        profile_button.setOnClickListener{
            if (seedPhrase.length == 0) {
                showSeedDialog()
            }
            //val intent = Intent(inflater.context, ProfileDetailActivity::class.java)
            //startActivity(intent)
            //var seedPhrase = profile_seed_phrase.editText?.text.toString()
            seedPhrase = "seed phrase"
            Log.d("@TWIG", seedPhrase)
            App.instance.onCreateWallet(seedPhrase)
            App.instance.synchronizer.status.collectWith(App.instance.appScope, ::onStatusUpdate)
            App.instance.synchronizer.clearedTransactions.collectWith(App.instance.appScope, ::onStatusTransaction)
        }

        return root
    }

    private fun onStatusTransaction(pagedList: PagedList<ConfirmedTransaction>) {
        val lastTransaction = pagedList.lastOrNull()
        Log.d("@TWIG", "memo = ${lastTransaction?.memo.toUtf8Memo()}")
    }

    private fun onStatusUpdate(status: Synchronizer.Status) {
        profile_address.text = status.name
    }

    inline fun ByteArray?.toUtf8Memo(): String {
        // TODO: make this more official but for now, this will do
        return if (this == null || this[0] >= 0xF5) "" else try {
            String(this, StandardCharsets.UTF_8).trim('\u0000')
        } catch (t: Throwable) {
            "unable to parse memo"
        }
    }

    private fun showSeedDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.seed_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.seed_login.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            seedPhrase = mDialogView.seed_words.text.toString()
        }
    }
}