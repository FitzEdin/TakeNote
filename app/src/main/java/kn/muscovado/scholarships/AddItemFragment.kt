package kn.muscovado.scholarships

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private val url = constants.BASE_URL + constants.PORT + constants.LINKS

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

        // set up utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        if (link.isNotEmpty()) {
            // Toasts for good/bad response
            val t = Toast.makeText(this.requireContext(), constants.SUCCESS_LINK_UPLOAD, Toast.LENGTH_LONG)
            t.setGravity(Gravity.CENTER, 0,0)
            val tError = Toast.makeText(this.requireContext(), constants.ERROR_LINK_UPLOAD, Toast.LENGTH_LONG)
            tError.setGravity(Gravity.CENTER, 0,0)

            // create JSONObject for uploading
            val jsonObject = JSONObject().put(constants.ITEM_LINK, link)
            Log.d(constants.LOG_TAG, jsonObject.toString())

            // create relevant POST request
            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                Response.Listener<JSONObject> { response ->
                    Log.d(constants.LOG_TAG, response.toString())
                    t.show()
                    add_item_text.hint = constants.DROP_ANOTHER_LINK
                    clear_item_btn.performClick()
                },
                Response.ErrorListener { err ->
                    Log.d(constants.LOG_TAG, err.toString())
                    tError.show()
                }
            )

            // send request
            requestQueue.add(request)
        }else{
            // ask person for a link
            val tEmpty = Toast.makeText(this.requireContext(), constants.QUESTION_LINK_DROP, Toast.LENGTH_LONG)
            tEmpty.setGravity(Gravity.CENTER, 0,0)
            tEmpty.show()
        }
    }
}
