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
import me.nestorbonilla.zact.utility.SampleStorageBridge

class App : Application() {

    var config = DemoConfig()
    var isCreated = true
    //private val config = this.defaultConfig

    //private val birthday = config.loadBirthday()

    lateinit var synchronizer: Synchronizer
    private lateinit var keyManager: SampleStorageBridge
    //private lateinit var initializer: Initializer
    //private lateinit var walletBirthday: Initializer.WalletBirthday

    var appScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        instance = this
        super.onCreate()
        Twig.plant(TroubleshootingTwig())
    }

    /*
    fun createInitializer() {
        initializer = Initializer(this, host = config.host, port = config.port)
        walletBirthday = config.newWalletBirthday()
        initializer.open(walletBirthday)
        synchronizer = Synchronizer(initializer)
        synchronizer.start(appScope)
    }

    fun openInitializer(seedPhrase: String) {
        val seed: ByteArray = Mnemonics.MnemonicCode(seedPhrase.toCharArray()).toSeed()
        var spendingKeys = initializer.new(seed, walletBirthday)
        keyManager = SampleStorageBridge().securelyStorePrivateKey(spendingKeys[0])
        synchronizer = Synchronizer(initializer)
        synchronizer.start(appScope)
    }*/

    fun onCreateWallet(seedPhrase: String) {

        val initializer = Initializer(this, host = config.host, port = config.port)

        if (isCreated) {
            initializer.open(config.newWalletBirthday())
        } else {
            val seed: ByteArray = Mnemonics.MnemonicCode(seedPhrase.toCharArray()).toSeed()
            var spendingKeys = initializer.new(seed, config.newWalletBirthday())
            keyManager = SampleStorageBridge().securelyStorePrivateKey(spendingKeys[0])
        }
        //val spendingKeys = initializer.new(seed, config.loadBirthday())

        synchronizer = Synchronizer(initializer)
        synchronizer.start(appScope)
    }

    companion object {
        lateinit var instance: App
    }

}
