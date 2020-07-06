package me.nestorbonilla.zact.fragment

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.db.entity.ConfirmedTransaction
import cash.z.ecc.android.sdk.ext.collectWith
import kotlinx.android.synthetic.main.seed_dialog.view.*
import me.nestorbonilla.zact.App
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.HomeActivity
import me.nestorbonilla.zact.model.AttendeeModel
import me.nestorbonilla.zact.model.CreatorModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import java.nio.charset.StandardCharsets


class SettingFragment : Fragment() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null
    private lateinit var creatorModel: CreatorModel
    private lateinit var attendeeModel: AttendeeModel

    private lateinit var profile_role_text: AppCompatTextView
    private lateinit var profile_action_text: AppCompatTextView
    private lateinit var profile_action_count_text: AppCompatTextView
    private lateinit var profile_address: AppCompatTextView
    private lateinit var profile_button: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        profile_role_text = root.findViewById(R.id.profile_role_text)
        profile_action_text = root.findViewById(R.id.profile_action_text)
        profile_action_count_text = root.findViewById(R.id.profile_action_count_text)
        profile_address = root.findViewById(R.id.profile_address)
        profile_button = root.findViewById(R.id.profile_button)

        db = ZactDatabase.getDatabase(requireContext())
        zactDao = db?.zactDao()

        loadValues()

        val profile_button: AppCompatButton = root.findViewById(R.id.profile_button)
        profile_button.setOnClickListener{

            // attendee profile
            if (creatorModel.seed.length == 0) {
                showSeedDialog()
            } else {
                creatorModel.seed = ""
                creatorModel.address = ""
                creatorModel.actsCreated = 0
                with(zactDao) {
                    this?.updateCreator(creatorModel)
                }
            }
        }
        return root
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
            //get text from EditTexts of custom layout
            creatorModel.seed = mDialogView.seed_words.text.toString()
            with(zactDao) {
                this?.updateCreator(creatorModel)
            }
            App.instance.onCreateWallet(creatorModel.seed)
            profile_button.setText("logout")
            profile_button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLogout))
            Toast.makeText(requireContext(), "You're now a creator!! Please, restart the app to create your first event.", Toast.LENGTH_LONG).show()
            mAlertDialog.dismiss()
        }
    }

    private fun loadValues() {
        with(zactDao) {
            creatorModel = this?.getCreator(1)!!
        }

        // verify if the user is a creator
        if (creatorModel.seed.isEmpty()) {

            // the user is an attendee
            with(zactDao) {
                attendeeModel = this?.getAttendee(1)!!
            }
            profile_role_text.setText("ZAct Attendee")
            profile_action_text.setText("Attendeed")
            profile_action_count_text.setText(attendeeModel.actsAttended.toString())
            profile_address.setText("only for creators")
            profile_button.setText("become a creator")
            profile_button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLogin))

        } else {
            profile_role_text.setText("ZAct Creator")
            profile_action_text.setText("Created")
            profile_action_count_text.setText(creatorModel.actsCreated.toString())
            profile_address.setText(creatorModel.address)
            profile_button.setText("logout")
            profile_button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorLogout))
        }
    }
}