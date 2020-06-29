package me.nestorbonilla.zact.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import cash.z.ecc.android.sdk.Initializer
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.ProfileDetailActivity

class ProfileFragment : Fragment() {

    private var seed: ByteArray = App.instance.defaultConfig.seed
    private val initializer: Initializer = Initializer(App.instance)
    private lateinit var address: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        address = initializer.deriveAddress(seed)
        val profile_address: TextView = root.findViewById(R.id.profile_address)
        profile_address.text = address

        val profile_button: AppCompatButton = root.findViewById(R.id.profile_button)
        profile_button.setOnClickListener{
            val intent = Intent(inflater.context, ProfileDetailActivity::class.java)
            startActivity(intent)
        }

        return root
    }
}