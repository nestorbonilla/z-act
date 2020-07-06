package me.nestorbonilla.zact.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_attendee_map.*
import kotlinx.android.synthetic.main.activity_creator_detail.*
import me.nestorbonilla.zact.utility.GeofenceHelper
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.utility.GeofenceBroadcastReceiver

class AttendeeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private var actId: Int = 0
    private lateinit var actModel: ActModel
    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

    private var GEOFENCE_ID = "2607"
    private var FINE_LOCATION_ACCESS_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendee_map)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()
        actId = intent.getIntExtra("act_id", 0)
        loadValues()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.attendee_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        // this need to be activated from the broadcast receiver
        activateUnlockButton()

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        attendee_detail_map_button.setOnClickListener({
            Observable.fromCallable(
                {
                    saveAct()
                }
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var currentLocation = LatLng(actModel.meetingPoint.split(",")[0].toDouble(), actModel.meetingPoint.split(",")[1].toDouble())

        //mMap.addMarker(MarkerOptions().position(panama).title("Marker in Panama"))
        addMarker(currentLocation)
        addCircle(currentLocation, actModel.meetingPointRadius.toFloat())
        addGeofence(currentLocation, actModel.meetingPointRadius.toFloat())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))

        enableUserLocation()
    }

    fun enableUserLocation() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for display why the permission is needed and then ask for the permission
                ActivityCompat.requestPermissions(this, Array(1) { android.Manifest.permission.ACCESS_FINE_LOCATION }, FINE_LOCATION_ACCESS_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(this, Array(1) { android.Manifest.permission.ACCESS_FINE_LOCATION }, FINE_LOCATION_ACCESS_REQUEST_CODE)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap.isMyLocationEnabled = true
            } else {
                //We don't have the permission
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(latLng: LatLng, radius: Float) {
        var geofence: Geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER)
        var geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)

        var pendingIntent: PendingIntent = geofenceHelper.getPendingIntent()
        //geofencingClient.addGeofences(geofencingRequest, pendingIntent)
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)?.run {
            addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            addOnCompleteListener {
                Log.d(TAG, "onComplete: Geofence completed...")
            }
            addOnFailureListener {
                var errorMessage: String = geofenceHelper.getErrorString(it)
                Log.d(TAG, "onFailure: " + errorMessage)
            }
        }
    }

    fun addMarker(latLng: LatLng) {
        var markerOptions: MarkerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
    }

    fun addCircle(latLng: LatLng, radius: Float) {
        var circleOptions: CircleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 0, 0, 0))
        circleOptions.fillColor(Color.argb(64, 0, 0, 0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

    private fun loadValues() {
        with(zactDao) {
            actModel = this?.getAct(actId)!!
        }
    }

    private fun activateUnlockButton() {
        attendee_detail_map_button.isEnabled = true
        attendee_detail_map_button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
    }

    private fun saveAct() {
        var missingWords = attendee_missing_text.text
        if (missingWords?.isEmpty()!!) {
            Toast.makeText(this, "Missing words cannot be empty.", Toast.LENGTH_SHORT)
        } else if(missingWords.split(" ").size != 2) {
            Toast.makeText(this, "You need to add the two missing words separated by space.", Toast.LENGTH_SHORT)
        } else {
            actModel.seed = actModel.seed + " " + missingWords
            with(zactDao) {
                this?.insertAct(actModel)
                finish()
            }
        }
    }
}