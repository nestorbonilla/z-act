package me.nestorbonilla.zact

import cash.z.ecc.android.sdk.Initializer
import me.nestorbonilla.zact.utility.SimpleMnemonics

data class DemoConfig(
    val host: String = "lightwalletd.z.cash",
    val port: Int = 9067,
    val birthdayHeight: Int = 835_000,
    val sendAmount: Double = 0.0018,

    // corresponds to address: zs1wk3v6543v08yah3tczk8sjxnlurm57g2rfvvjdres4zd7vw37xxlk4e3c3x3pr68staqk5r34un
    val seedWords: String = "inflict canyon owner clog link divert gym ride resist ethics dust hazard run enemy venue weather unaware assist pipe salute damage burden observe notable",

    // corresponds to seed: urban kind wise collect social marble riot primary craft lucky head cause syrup odor artist decorate rhythm phone style benefit portion bus truck top
    val toAddress: String = "zs1lcdmue7rewgvzh3jd09sfvwq3sumu6hkhpk53q94kcneuffjkdg9e3tyxrugkmpza5c3c5e6eqh"
) {
    val seed: ByteArray get() = SimpleMnemonics().toSeed(seedWords.toCharArray())
    fun newWalletBirthday() = Initializer.DefaultBirthdayStore.loadBirthdayFromAssets(App.instance)
    fun loadBirthday(height: Int = birthdayHeight) = Initializer.DefaultBirthdayStore.loadBirthdayFromAssets(App.instance, height)
}
