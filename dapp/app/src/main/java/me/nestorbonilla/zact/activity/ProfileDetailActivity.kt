package me.nestorbonilla.zact.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Synchronizer
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.utility.SampleStorageBridge

class ProfileDetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_detail)


    }


}