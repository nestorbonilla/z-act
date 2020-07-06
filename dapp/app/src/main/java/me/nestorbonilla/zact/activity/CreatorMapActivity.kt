package me.nestorbonilla.zact.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_creator_map.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.ApiPutResponseModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.service.ServiceBuilder
import me.nestorbonilla.zact.service.ZactService
import me.nestorbonilla.zact.utility.GeofenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatorMapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    SeekBar.OnSeekBarChangeListener {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

    private var progressView: TextView? = null
    private var seekbarView: SeekBar? = null

    private var TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private var GEOFENCE_ID = "SOME_GEOFENCE_ID"
    private var FINE_LOCATION_ACCESS_REQUEST_CODE = 1001

    private var currentId = 0
    private var currentLocation = LatLng(0.0, 0.0)
    private var currentRadius = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creator_map)

        db = ZactDatabase.getDatabase(this)
        zactDao = db?.zactDao()

        progressView = this.creator_progress
        seekbarView = this.creator_seekbar
        seekbarView!!.setOnSeekBarChangeListener(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.creator_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        loadValues()

        creator_detail_map_button.setOnClickListener({
            Observable.fromCallable(
                {
                    saveMapLocation()
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

        val panama = LatLng(9.000758, -79.530742)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(panama, 16F))
        //if ((currentLocation.latitude > 0 || currentLocation.latitude < 0) && currentRadius > 0) {
        //    loadMapValues()
        //} else {
        //    val panama = LatLng(9.000758, -79.530742)
        //    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(panama, 16F))
        //}

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
        currentLocation = latLng
        addMarker(latLng)
        addCircle(latLng, progressView!!.text.toString().toFloat())
        addGeofence(latLng, progressView!!.text.toString().toFloat())
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
        circleOptions.strokeColor(Color.argb(255, 35, 31, 32))
        circleOptions.fillColor(Color.argb(64, 35, 31, 32))
        circleOptions.strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        progressView!!.text = progress.toString()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

    private fun saveMapLocation() {

        val zactService = ServiceBuilder.buildService(ZactService::class.java)

        var locationToUpdate = currentLocation
        var radiusToUpdate = progressView!!.text.toString().toFloat()
        Log.d(TAG, "current radius: " + radiusToUpdate)

        // validate that there's a radious to update
        if (radiusToUpdate == 0.0F) {
            runOnUiThread {
                val toast =
                    Toast.makeText(applicationContext, "Please, select a radius.", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        // validate that there's a geofence to update
        else if (locationToUpdate.latitude == 0.0 || locationToUpdate.longitude == 0.0) {
            runOnUiThread {
                val toast =
                    Toast.makeText(applicationContext, "Please, select one point on the map.", Toast.LENGTH_SHORT)
                toast.show()
            }
        } else {
            with(zactDao) {
                var act = this?.getAct(currentId)
                if (act != null) {

                    act.meetingPointRadius = radiusToUpdate.toInt()
                    act.meetingPoint = locationToUpdate.latitude.toString() + "," + locationToUpdate.longitude.toString()

                    var actApi = ActModel(
                        act.id,
                        act._id,
                        act.fromAddress,
                        "",
                        act.seed.split(" ").slice(0..21).toString().replace(",", "").replace("[", "").replace("]", ""),
                        act.title,
                        act.publicInformation,
                        act.meetingPointRadius,
                        act.meetingPoint
                    )

                    val requestCall = zactService.updateAct(actApi._id, actApi)

                    requestCall.enqueue(object: Callback<ApiPutResponseModel> {
                        override fun onResponse(call: Call<ApiPutResponseModel>, response: Response<ApiPutResponseModel>) {
                            if (response.isSuccessful) {
                                var result = response.body()
                                if (result != null) {
                                    if (result.nModified == 1) { //validate if the record was updated
                                        with(zactDao) {
                                            this?.insertAct(act)
                                            finish()
                                        }
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    val toast =
                                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                                    toast.show()
                                }
                            }
                        }
                        override fun onFailure(call: Call<ApiPutResponseModel>, t: Throwable) {
                            runOnUiThread {
                                val toast =
                                    Toast.makeText(applicationContext,t.localizedMessage, Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    })
                }
            }
        } /*else {
            runOnUiThread {
                val toast =
                    Toast.makeText(this,"Please, select a Radius and place one point on the map.", Toast.LENGTH_SHORT)
                toast.show()
            }
        }*/
    }

    private fun loadValues() {

        currentId = intent.getIntExtra("act_id", 0)
        Log.d(TAG, "current id: " + currentId)
        if (currentId > 0) {
            currentRadius = intent.getIntExtra("act_radius", 0).toFloat()
            Log.d(TAG, "current radius: " + currentRadius)
            var actCoordinate = intent.getStringExtra("act_coordinatee")
            Log.d(TAG, "current coordinate: " + actCoordinate)
            if (!actCoordinate.isEmpty()) {
                currentLocation = LatLng(actCoordinate.split(",")[0].toDouble(), actCoordinate.split(",")[1].toDouble())
            }
        }
    }

    private fun loadMapValues() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16F))

        //seekbarView?.setProgress(currentRadius, true)
        //progressView?.setText(currentRadius)

        Log.d(TAG, "adding marker: " + currentLocation.latitude)
        addMarker(currentLocation)
        Log.d(TAG, "adding circle: " + currentLocation.latitude)
        addCircle(currentLocation, currentRadius)
        Log.d(TAG, "adding geofence: " + currentLocation.latitude)
        addGeofence(currentLocation, currentRadius)
    }
}