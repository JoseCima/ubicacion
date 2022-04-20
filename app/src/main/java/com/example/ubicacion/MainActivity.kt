package com.example.ubicacion

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val CODIGO_SOLICITUD_PERMISO = 100

    var fusedLocationClient: FusedLocationProviderClient? = null

    var locationRequest:LocationRequest? = null

    var callback:LocationCallback? = null



    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()
    }

    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.intervalMillis = 10000
        locationRequest?.durationMillis = 5000
        locationRequest?.quality = LocationRequest.QUALITY_HIGH_ACCURACY
    }



    private fun validarpermisosUbicacion():Boolean{

        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(this, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(this, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){


        /*fusedLocationClient?.lastLocation?.addOnSuccessListener (this, object: OnSuccessListener<Location>{
            override fun onSuccess(location: Location?) {
                if(location != null){
                    Toast.makeText(applicationContext, location?.latitude.toString() + " - " + location?.longitude, Toast.LENGTH_LONG).show()
                }
            }

        })*/
        callback = object:LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for(ubicacion in locationResult?.locations!!){
                    Toast.makeText(applicationContext, ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        fusedLocationClient?.removeLocationUpdates(locationRequest, callback, null)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pedirPermisos(){

        //validar si ya tiene o ya nego los permisos
        var deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this, permisoFineLocation)//Permite validar si el usuario nega o acepta los permisos, por lo tanto debemos dar mas infromacion sobre pq ocupamos dichos permisos

        if(deboProveerContexto){
            //mandar mensaje con informacion adicional
            solicitudPermiso()

        }else{
            solicitudPermiso()
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun solicitudPermiso(){
        requestPermissions(arrayOf(permisoFineLocation, permisoCoarseLocation), CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CODIGO_SOLICITUD_PERMISO->{
                if(grantResults.size > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //obtener ubicacion
                }else{
                    Toast.makeText(this, "No diste permiso para obtener ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun detenerActualizacionDeUbicacion() {
        fusedLocationClient?.removeLocationUpdates(callback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        if(validarpermisosUbicacion()){
            obtenerUbicacion()
        }else{
            pedirPermisos()
        }
    }

    //Deteniendo la actualizacion de la ubicacion
    override fun onPause() {
        super.onPause()
        detenerActualizacionDeUbicacion()
    }
}

