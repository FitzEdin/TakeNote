package kn.muscovado.scholarships

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.NonNull
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonArrayRequest
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.scholarships.content.Item
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var rcvr : BroadcastReceiver = makeBroadcastReceiver()
    private var realm = Realm.getDefaultInstance()
    private val constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isConnected()) {    getItems()    }
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

                val msg = when (isConnected()) {
                    true -> "Online"
                    else -> "No network"
                }

                val t = Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
                t.setGravity(Gravity.CENTER, 0, 0)
                t.show()
            }
        }

    }

    private fun getItems() {
        val url = constants.BASE_URL + constants.PORT + constants.SCHOLARSHIPS
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,

            // handle positive response
            Response.Listener<JSONArray> { response: JSONArray ->
                Log.d(constants.LOG_TAG, response.toString())

                // Clear existing Realm
                realm.beginTransaction()
                realm.where<Item>().findAll().deleteAllFromRealm()
                realm.commitTransaction()

                for (i in 0 until response.length()) {
                    //extract JSON object from response & convert to Kotlin object
                    val bob = getItem(response.getJSONObject(i))
                    //and add to list of Items
                    realm.beginTransaction()
                    realm.copyToRealm(bob)
                    realm.commitTransaction()
                }

                realm.close()
            },

            // Log an error response
            Response.ErrorListener { err ->
                Log.e(constants.LOG_TAG, err.toString())
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    // Parse objects from array
    @NonNull
    @Throws(JSONException::class)
    private fun getItem(bob: JSONObject): Item {
        if (bob.getString(constants.ITEM_STATUS) == constants.STATUS_VETTED) {
            Log.d("getItem()", bob.getString(constants.ITEM_TITLE))

            return Item(
                bob.getString(constants.ITEM_ID),
                bob.getString(constants.ITEM_TITLE),
                bob.getString(constants.ITEM_COVERAGE),
                bob.getString(constants.ITEM_LEVEL),
                bob.getString(constants.ITEM_PROGRAMME),
                bob.getString(constants.ITEM_LOCATION),
                bob.getString(constants.ITEM_OPEN_TO),
                bob.getString(constants.ITEM_LINK),
                bob.getString(constants.ITEM_STATUS)
            )
        }else {
            Log.d("getItem()", bob.getString(constants.ITEM_ID))

            return Item(
                bob.getString(constants.ITEM_ID),
                constants.UNVETTED_ITEM,
                constants.UNVETTED_ITEM,
                constants.UNVETTED_ITEM,
                constants.UNVETTED_ITEM,
                constants.UNVETTED_ITEM,
                constants.UNVETTED_ITEM,
                bob.getString(constants.ITEM_LINK),
                bob.getString(constants.ITEM_STATUS)
            )
        }
    }
}
