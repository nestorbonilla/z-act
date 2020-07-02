package me.nestorbonilla.zact.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.paging.PagedList
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.db.entity.ConfirmedTransaction
import cash.z.ecc.android.sdk.ext.collectWith
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.onEach
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.ProfileDetailActivity
import java.nio.charset.StandardCharsets

class ProfileFragment : Fragment() {

    private lateinit var address: String
    private lateinit var profile_address: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        profile_address = root.findViewById(R.id.profile_address)
        //profile_address.text = address

        val profile_seed_phrase: TextInputLayout = root.findViewById(R.id.profile_seed_phrase)

        //val profile_seed_phrase: String = ""

        val profile_button: AppCompatButton = root.findViewById(R.id.profile_button)
        profile_button.setOnClickListener{
            //val intent = Intent(inflater.context, ProfileDetailActivity::class.java)
            //startActivity(intent)
            var seedPhrase = profile_seed_phrase.editText?.text.toString()
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
}