package kn.muscovado.scholarships


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonArrayRequest
import kn.muscovado.scholarships.content.Item
import kotlinx.android.synthetic.main.fragment_items.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import io.realm.Realm
import io.realm.kotlin.where

class ItemsFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private val constants = Constants()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // TODO: Alternatively, query the API for new scholarships, and get the new ones
        getItems()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_items.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_searchItemsFrag)
        )
        new_item.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_addItemFrag)
        )

        if (listView is RecyclerView) {
            with(listView) {
                layoutManager = LinearLayoutManager(context)
                adapter = ItemRVAdapter()
            }
        }

        if (realm.where<Item>().findAll().size > 0) {
            listView.visibility = View.VISIBLE
            empty_items_list.visibility = View.GONE
        }
        else {
            listView.visibility = View.GONE
            empty_items_list.visibility = View.VISIBLE
        }
    }

    private fun getItems() {
        val url = constants.BASE_URL + constants.PORT + constants.SCHOLARSHIPS
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,

            // handle positive response
            Response.Listener<JSONArray> { response: JSONArray ->
                Log.d(constants.LOG_TAG, response.toString())

                // Clear existing Realm
                realm.beginTransaction()
                realm.deleteAll()
                realm.commitTransaction()

                for (i in 0 until response.length()) {
                    //extract JSON object from response & convert to Kotlin object
                    val bob = getItem(response.getJSONObject(i))
                    //and add to list of Events
                    realm.beginTransaction()
                    realm.copyToRealm(bob)
                    realm.commitTransaction()

                    listView.visibility = View.VISIBLE
                    empty_items_list.visibility = View.GONE
                }

                realm.close()
            },

            // Log an error response
            Response.ErrorListener { err ->
                Log.e(constants.LOG_TAG, err.toString())
                listView.visibility = View.GONE
                empty_items_list.visibility = View.VISIBLE
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    // Parse objects from array
    @NonNull
    @Throws(JSONException::class)
    private fun getItem(bob: JSONObject): Item {
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
    }
}
