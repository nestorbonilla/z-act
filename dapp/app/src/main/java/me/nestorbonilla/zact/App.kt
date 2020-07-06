package me.nestorbonilla.zact

import android.app.Application
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.Initializer
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.ext.TroubleshootingTwig
import cash.z.ecc.android.sdk.ext.Twig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class App : Application() {

    var config = DemoConfig()
    var synchronizer: Synchronizer? = null

    var appScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        instance = this
        super.onCreate()
        Twig.plant(TroubleshootingTwig())
    }

    fun onOpenWallet() {
        val initializer = Initializer(this, host = config.host, port = config.port)
        initializer.open(config.newWalletBirthday())
        synchronizer = Synchronizer(initializer)
        synchronizer?.start(appScope)
    }

    fun onCreateWallet(seedPhrase: String) {
        val initializer = Initializer(this, host = config.host, port = config.port)
        val seed: ByteArray = Mnemonics.MnemonicCode(seedPhrase.toCharArray()).toSeed()
        initializer.new(seed, config.newWalletBirthday())
    }

    companion object {
        lateinit var instance: App
    }

}
