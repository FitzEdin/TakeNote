package kn.muscovado.scholarships.admin


import android.os.Bundle
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
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.scholarships.R
import kn.muscovado.scholarships.content.Item
import kn.muscovado.scholarships.utils.Constants
import kotlinx.android.synthetic.main.fragment_edit_item.*
import org.json.JSONObject

/**
 * Used to edit an item in the list and save it to the DB.
 */
class EditItemFragment : Fragment() {
    private val constants = Constants()
    private var realm = Realm.getDefaultInstance()
    private val url = constants.BASE_URL + constants.PORT + constants.SCHOLARSHIPS
    private var item:Item? = Item()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id =
            if (arguments?.getString(constants.TAG_ITEM) != null)
                arguments?.getString(constants.TAG_ITEM)!!
            else throw NullPointerException("Expression 'arguments?.getString(constants.TAG_ITEM)' must not be null")

        val realm = Realm.getDefaultInstance()
        item = realm.where<Item>()
            .equalTo(constants.ITEM_ID, id)
            .findFirst()

        populateForm()

        save_item_btn.setOnClickListener{
            saveItem()
            uploadItem()
        }

        delete_item_btn.setOnClickListener{
            deleteItem()
        }

        // set up close button
        close_item_edit.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_editItemFrag_pop)
        )
    }

    // populate the form UI with info from the Item
    private fun populateForm() {
        edit_item_title.setText(item?.title)
        edit_item_link.setText(item?.link)
        edit_item_location.setText(item?.location)
        edit_item_programme.setText(item?.programme)
        edit_item_open_to.setText(item?.open_to)

        // radio buttons
        when(item?.coverage) {
            constants.FULL -> full_coverage_rbtn.isChecked = true
            constants.PARTIAL -> partial_coverage_rbtn.isChecked = true
            constants.TUITION -> tuition_coverage_rbtn.isChecked = true
        }

        // check boxes
        when(item?.level) {
            constants.LEVEL_FOUR -> four_level_check.isChecked = true
            constants.LEVEL_THREE -> three_level_check.isChecked = true
            constants.LEVEL_TWOB,
            constants.LEVEL_TWO -> two_level_check.isChecked = true
            constants.LEVEL_ONE -> one_level_check.isChecked = true
            constants.LEVEL_ZERO -> zero_level_check.isChecked = true
        }

        // switch
        edit_item_status.isChecked = when(item?.status) {
            constants.STATUS_VETTED -> true
            else -> false
        }
    }

    // save changes to item when button clicked
    private fun saveItem(){
        Toast.makeText(this.requireContext(), "Saving Item", Toast.LENGTH_LONG).show()

        realm.beginTransaction()

        // save info from textfields
        item?.title = edit_item_title.text.toString()
        item?.link = edit_item_link.text.toString()
        item?.location = edit_item_location.text.toString()
        item?.programme = edit_item_programme.text.toString()
        item?.open_to = edit_item_open_to.text.toString()

        // save info from radio button
        if(full_coverage_rbtn.isChecked){
            item?.coverage = constants.FULL
        }else if(partial_coverage_rbtn.isChecked){
            item?.coverage = constants.PARTIAL
        }else if(tuition_coverage_rbtn.isChecked){
            item?.coverage = constants.TUITION
        }

        // save info from switch
        item?.status = when(edit_item_status.isChecked) {
            true -> constants.STATUS_VETTED
            false -> constants.STATUS_NEW
        }

        realm.commitTransaction()

    }

    // upload the info from Item to database
    private fun uploadItem(){
        Toast.makeText(this.requireContext(), "Uploading Item", Toast.LENGTH_LONG).show()

        // create toasts for good/bad response
        val t = Toast.makeText(this.requireContext(), constants.SUCCESS_SCHOL_UPLOAD, Toast.LENGTH_LONG)
        t.setGravity(Gravity.CENTER, 0,0)
        val tError = Toast.makeText(this.requireContext(), constants.ERROR_SCHOL_UPLOAD, Toast.LENGTH_LONG)
        tError.setGravity(Gravity.CENTER, 0,0)

        // set up network utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        // create JSONObject for uploading
        val jsonObject = JSONObject()
            .put(constants.ITEM_TITLE, item?.title)
            .put(constants.ITEM_COVERAGE, item?.coverage)
            .put(constants.ITEM_LEVEL, item?.level)
            .put(constants.ITEM_PROGRAMME, item?.programme)
            .put(constants.ITEM_LOCATION, item?.location)
            .put(constants.ITEM_OPEN_TO, item?.open_to)
            .put(constants.ITEM_LINK, item?.link)
            .put(constants.ITEM_STATUS, item?.status)
        Log.d(constants.LOG_TAG, jsonObject.toString())

        // create relevant PUT request to update item
        val request = JsonObjectRequest(
            Request.Method.PUT, url + "/" + item?._id, jsonObject,
            Response.Listener<JSONObject> { response ->
                Log.d(constants.LOG_TAG, response.toString())
                //TODO: parse the response here (or in the API) for DB errors
                t.show()
            },
            Response.ErrorListener { err ->
                Log.d(constants.LOG_TAG, err.toString())
                tError.show()
            }
        )

        // send request
        requestQueue.add(request)
    }

    private fun deleteItem() {
        //confirm delete first
        //delete if confirmed
        Toast.makeText(this.requireContext(), "Deleting Item", Toast.LENGTH_LONG).show()

        // create toasts for good/bad response
        val t = Toast.makeText(this.requireContext(), constants.SUCCESS_SCHOL_DELETE, Toast.LENGTH_LONG)
        t.setGravity(Gravity.CENTER, 0,0)
        val tError = Toast.makeText(this.requireContext(), constants.ERROR_SCHOL_DELETE, Toast.LENGTH_LONG)
        tError.setGravity(Gravity.CENTER, 0,0)

        // set up network utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        // create relevant PUT request to update item
        val request = JsonObjectRequest(
            Request.Method.DELETE, url + "/" + item?._id, null,
            Response.Listener<JSONObject> { response ->
                Log.d(constants.LOG_TAG, response.toString())
                //TODO: parse the response here (or in the API) for DB errors
                t.show()
            },
            Response.ErrorListener { err ->
                Log.d(constants.LOG_TAG, err.toString())
                tError.show()
            }
        )

        // send request
        requestQueue.add(request)

    }

}
