package com.example.apptiflobook

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.apptiflobook.databinding.ActivityMainBinding
import com.ingenieriajhr.blujhr.BluJhr

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tangible: String = "Tiflopoesia\n30:AE:A4:1E:6A:72" //required device

    lateinit var blue: BluJhr //blue is Bluetooth object
    var devicesBluetooth = ArrayList<String>() //paired bluetooth devices founded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blue = BluJhr(this) //blue is initialized
        blue.onBluetooth()

    } //close onCreate

    /**
     * This method ask for bluetooth permissions on Android device.
     * Android 12 permission needed is BLUETOOTH_SCAN and BLUETOOTH_CONNECT
     * Android 12 o superior needed aditional permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show()
            blue.initializeBluetooth() //bluetooth is connected
            Toast.makeText(this, "bluetooth se inicializó", Toast.LENGTH_SHORT).show()
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth() //bluetooth is connected
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth()
        }else{
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth()
                if (devicesBluetooth.isNotEmpty()){
                    //aquí se tiene la lista de devices on Bluetooth, debo identificar el que me interesa
                    identifyingDevice()
                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun identifyingDevice() {
        if (devicesBluetooth.isNotEmpty()) {
            blue.connect(tangible)
            blue.setDataLoadFinishedListener(object : BluJhr.ConnectedBluetooth {
                override fun onConnectState(state: BluJhr.Connected) {
                    when (state) {

                        BluJhr.Connected.True -> {
                            Toast.makeText(applicationContext, "True", Toast.LENGTH_SHORT)
                                .show()
                            binding.consola.text=tangible
                            rxReceived()
                        }

                        BluJhr.Connected.Pending -> {
                            Toast.makeText(applicationContext, "Pending", Toast.LENGTH_SHORT)
                                .show()
                        }

                        BluJhr.Connected.False -> {
                            Toast.makeText(applicationContext, "False", Toast.LENGTH_SHORT)
                                .show()
                        }

                        BluJhr.Connected.Disconnect -> {
                            Toast.makeText(applicationContext, "Disconnect", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                }
            })
        }
    }

    private fun rxReceived() {
        blue.loadDateRx(object:BluJhr.ReceivedData{
            override fun rxDate(rx: String) {
                binding.consola.text = "Datos recibidos: " + rx
            }
        })
    }

}//close Activity class