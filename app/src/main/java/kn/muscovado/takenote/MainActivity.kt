package kn.muscovado.takenote


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.content.Item
import kn.muscovado.takenote.utils.Constants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var rcvr : BroadcastReceiver = makeBroadcastReceiver()
    private val constants = Constants()
    private var snackColor = 0

    private var realm = Realm.getDefaultInstance()

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

                val s = Snackbar.make(
                    findViewById(R.id.fragment),
                    getMsg(),
                    Snackbar.LENGTH_LONG
                )
                s.view.setBackgroundColor(snackColor)
                s.show()
            }
        }

    }

    private fun getMsg() : String {
        return when (isConnected()) {
            true -> {
                snackColor = resources.getColor(R.color.colorPrimaryDarker)
                constants.BANNER_ONLINE
            }
            else -> {
                snackColor = resources.getColor(R.color.colorIconDanger)
                constants.BANNER_OFFLINE
            }
        }
    }

    private fun getItems() {
        val url = constants.BASE_URL + constants.PORT + constants.NOTICES
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,

            // handle positive response
            { response: JSONArray ->
                Log.d(constants.LOG_TAG, response.toString())

                /** TODO: remove items one by one */
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
            { err ->
                Log.e(constants.LOG_TAG, err.toString())
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    // Parse objects from array
    @NonNull
    @Throws(JSONException::class)
    private fun getItem(bob: JSONObject): Item {

        return Item(
            bob.getString(constants.ITEM_ID),
            bob.getString(constants.ITEM_TERRITORY),
            bob.getString(constants.ITEM_DEPARTMENT),
            bob.getString(constants.ITEM_DATE),
            bob.getString(constants.ITEM_VENUE),
            bob.getString(constants.ITEM_DESCRIPTION),
            bob.getString(constants.ITEM_STATUS)
        )
    }
}
