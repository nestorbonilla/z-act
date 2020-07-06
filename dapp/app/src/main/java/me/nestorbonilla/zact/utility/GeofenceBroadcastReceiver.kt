package me.nestorbonilla.zact.utility

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import me.nestorbonilla.zact.activity.AttendeeMapActivity


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        var event = GeofencingEvent.fromIntent(intent)
        if(event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            var newIntent: Intent = Intent("zactgeofence")
            LocalBroadcastManager.getInstance(context.applicationContext).sendBroadcast(newIntent)
        }

    }
}
