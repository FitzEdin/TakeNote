package kn.muscovado.scholarships

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var rcvr : BroadcastReceiver = makeBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(rcvr, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(rcvr)
    }

    private fun isConnected() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun makeBroadcastReceiver() : BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                val networkStateExtra = intent.getBooleanExtra(
//                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, true
//                )

                val msg = when (isConnected()) {
                    true -> "Online"
                    else -> "No network"
                }
//                val wifiStateExtra = intent.getIntExtra(
//                    WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
//
//                when (wifiStateExtra) {
//                    WifiManager.WIFI_STATE_ENABLED,
//                    WifiManager.WIFI_STATE_ENABLING
//                        -> msg = "Wifi on"
//                    WifiManager.WIFI_STATE_DISABLED,
//                    WifiManager.WIFI_STATE_DISABLING
//                        -> msg = "Wifi off"
//                }

                val t = Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }

    }
}
