package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var btAdapter: BluetoothAdapter
    private val requestBT = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        // Register for broadcasts when a device is discovered.
        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))

    }

    fun requestDiscoverable ( v:View){
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600)
        startActivity(discoverableIntent)
    }

    fun setName( v:View){
        if (init()) {
            btAdapter.setName("IMEI:" + System.currentTimeMillis().toString())
            tView.setText ( "ok")
        }
    }

    fun start( v:View){
        if (init()) {

            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery()
            }

            btAdapter.startDiscovery()
        }

    }

    fun init() :Boolean{
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = bluetoothManager.adapter

        if (btAdapter == null || !btAdapter.isEnabled) {
            tView.setText ( "bluetoothAdapter problem... \n")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, requestBT)
            return false
        }

        tView.setText ( btAdapter.name + ". " + btAdapter.scanMode + " \n")

        return true
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            tView.setText ( tView.text.toString() +"---\n ")
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE)
                    val name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)

                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    tView.setText ( tView.text.toString() + name +
                            ":" + rssi.toString() + "db \n" +
                            device.address + " \n")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }


}