package kn.muscovado.takenote.items


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
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import kn.muscovado.takenote.R
import kn.muscovado.takenote.utils.Constants
import kotlinx.android.synthetic.main.fragment_add_item.*
import org.json.JSONObject

/**
 * Handles uploading an item to the API
 *
 * Adds an item to the API using information collected
 * in a corresponding form
 */
class AddItemFragment : Fragment() {
    private val constants = Constants()
    private val url = constants.BASE_URL + constants.PORT + constants.NOTICES
    private var num = 0
    private var snackColor = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up back button
        back_item_add.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_addItemFrag_pop)
        )

        // add object that listens for changes to text
//        add_item_description.addTextChangedListener(addItemTextWatcher)
//        add_item_department.addTextChangedListener(addItemTextWatcher)
//        add_item_territory.addTextChangedListener(addItemTextWatcher)
//        add_item_venue.addTextChangedListener(addItemTextWatcher)
//        add_item_date.addTextChangedListener(addItemTextWatcher)


        // set up button to remove text
        clear_item_btn.setOnClickListener{ clearAll() }
//        clear_item_btn.visibility = View.INVISIBLE

        // add action when addButton is tapped
        add_item_btn.setOnClickListener { addItem() }
//        add_item_btn.visibility = View.INVISIBLE
    }

    // listens for changes to textView
    private val addItemTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // show/hide button to remove text when there is text/no text
            if (!add_item_description.text.isEmpty()) clear_item_btn.visibility = View.VISIBLE
            else clear_item_btn.visibility = View.INVISIBLE
        }

        override fun afterTextChanged(s: Editable) {
            when (s.isEmpty()) {
                true -> num--
                false -> num++
            }
            if (num < 0) num = 0
            if (num > 5) num = 5

            if (num == 5) add_item_btn.visibility = View.VISIBLE
            else add_item_btn.visibility = View.INVISIBLE

            if (num > 0) clear_item_btn.visibility = View.VISIBLE
            else clear_item_btn.visibility = View.INVISIBLE
        }
    }

    // upload item information or ask for a link
    private fun addItem() {
        // set up utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        // create relevant POST request
        val request = JsonObjectRequest(
            Request.Method.POST, url, buildJSONObject(),
            Response.Listener { response ->
                Log.d(constants.LOG_TAG, response.toString())
                //TODO: parse the response here (or in the API) for DB errors
                buildSnackBar(true).show()
                clear_item_btn.performClick()
            },
            Response.ErrorListener { err ->
                Log.d("Add Response", err.toString())
                buildSnackBar(false).show()
            }
        )

        // send request
        requestQueue.add(request)

        /**if (description.isNotEmpty()) {
        }else{
            // ask person for a link
            val tEmpty = Toast.makeText(this.requireContext(), constants.QUESTION_LINK_DROP, Toast.LENGTH_LONG)
            tEmpty.setGravity(Gravity.CENTER, 0,0)
            tEmpty.show()
        } **/
    }

    private fun clearAll() {
        add_item_description.text.clear()
        add_item_department.text.clear()
        add_item_territory.text.clear()
        add_item_venue.text.clear()
        add_item_date.text.clear()
    }

    private fun buildJSONObject(): JSONObject {
        return JSONObject()
            .put(constants.ITEM_DESCRIPTION, add_item_description.toString())
            .put(constants.ITEM_DEPARTMENT, add_item_department.toString())
            .put(constants.ITEM_TERRITORY, add_item_territory.toString())
            .put(constants.ITEM_VENUE, add_item_venue.toString())
            .put(constants.ITEM_DATE, add_item_date.toString())
    }

    private fun buildSnackBar(success: Boolean): Snackbar {
        val msg = when (success) {
            true -> {
                snackColor = resources.getColor(R.color.colorPrimary)
                constants.SUCCESS_LINK_UPLOAD
            }
            false -> {
                snackColor = resources.getColor(R.color.colorIconDanger)
                constants.ERROR_LINK_UPLOAD
            }
        }

        val s = Snackbar.make(
            activity?.findViewById(R.id.frag_add_item)!!,
            msg,
            Snackbar.LENGTH_LONG
        )
        s.view.setBackgroundColor(snackColor)

        return s
    }
}
