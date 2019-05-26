package kn.muscovado.scholarships

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import kotlinx.android.synthetic.main.fragment_add_item.*
import org.json.JSONObject

/**
 * Handles uploading an item to the API
 */
class AddItemFragment : Fragment() {
    private val constants = Constants()
    private val itemUrl = "/links"
    private val url = constants.BASE_URL + constants.PORT + itemUrl

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_item_add.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_addItemFrag_pop)
        )

        clear_item_btn.setOnClickListener { add_item_text.text.clear() }
        clear_item_btn.visibility = View.INVISIBLE

        add_item_text.addTextChangedListener(addItemTextWatcher)

        add_item_btn.setOnClickListener { addItem(add_item_text.text.toString()) }
    }

    private val addItemTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!add_item_text.text.isEmpty()) clear_item_btn.visibility = View.VISIBLE
            else clear_item_btn.visibility = View.INVISIBLE
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun addItem(link: String) {
        val jsonObject = JSONObject().put(constants.ITEM_LINK, link)
        Log.d("Network", jsonObject.toString())

        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                Log.d("Network", response.toString())
            },
            Response.ErrorListener { err ->
                Log.d("Network", err.toString())
            }
        )

        requestQueue.add(request)
    }
}
