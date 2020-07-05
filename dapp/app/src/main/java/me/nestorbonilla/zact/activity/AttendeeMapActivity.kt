package me.nestorbonilla.zact.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import me.nestorbonilla.zact.utility.GeofenceHelper
import me.nestorbonilla.zact.R

class AttendeeMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private var TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private var GEOFENCE_RADIUS = 200F
    private var GEOFENCE_ID = "SOME_GEOFENCE_ID"
    private var FINE_LOCATION_ACCESS_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendee_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
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

        // Add a marker in Eiffel Tower and move the camera
        val panama = LatLng(9.000758, -79.530742)
        mMap.addMarker(MarkerOptions().position(panama).title("Marker in Panama"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(panama, 16F))

        enableUserLocation()
        mMap.setOnMapLongClickListener(this)
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

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENCE_RADIUS)
        addGeofence(latLng, GEOFENCE_RADIUS)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(latLng: LatLng, radius: Float) {
        var geofence: Geofence = geofenceHelper
            .getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER)
        var geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        Log.d(TAG, "get pendingIntent")
        var pendingIntent: PendingIntent = geofenceHelper.getPendingIntent()
        //geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener()
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)?.run {
            addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
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
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }
}