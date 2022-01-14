package kn.muscovado.takenote.admin


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kn.muscovado.takenote.databinding.FragmentEditItemBinding
import kn.muscovado.takenote.utils.Constants
import org.json.JSONObject

/**
 * Used to edit an item in the list and save it to the DB.
 */
class EditItemFragment : Fragment() {
    private val constants = Constants()
    private var realm = Realm.getDefaultInstance()
    private val url = constants.BASE_URL + constants.PORT + constants.NOTICES
    private var item:Item? = Item()

    private var colorPrimary = 0
    private var colorDanger = 0

    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // set toast colours
        colorPrimary = resources.getColor(R.color.colorPrimaryDarker)
        colorDanger = resources.getColor(R.color.colorIconDanger)

        // Inflate layout for this fragment
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.saveItemBtn.setOnClickListener{
            saveItem()
            uploadItem()
        }

        binding.deleteItemBtn.setOnClickListener{
            deleteItem()
        }

        // set up close button
        binding.closeItemEdit.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_editItemFrag_pop)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // populate the form UI with info from the Item
    private fun populateForm() {
        binding.addItemDescription.setText(item?.description)
        binding.editItemDepartment.setText(item?.department)
        binding.editItemTerritory.setText(item?.territory)
        binding.editItemVenue.setText(item?.venue)
        binding.editItemDate.setText(item?.date)

        // switch
        binding.editItemStatus.isChecked = when(item?.status) {
            constants.STATUS_VETTED -> true
            else -> false
        }
    }

    // save changes to item when button clicked
    private fun saveItem(){
        getSnackbar(constants.SAVING_ITEM, colorPrimary).show()

        realm.beginTransaction()

        // save info from textfields
        item?.description = binding.addItemDescription.text.toString()
        item?.department = binding.editItemDepartment.text.toString()
        item?.territory = binding.editItemTerritory.text.toString()
        item?.venue = binding.editItemVenue.text.toString()
        item?.date = binding.editItemDate.text.toString()

        // save info from switch
        item?.status = when(binding.editItemStatus.isChecked) {
            true -> constants.STATUS_VETTED
            false -> constants.STATUS_UNVETTED
        }

        realm.commitTransaction()

    }

    // upload the info from Item to database
    private fun uploadItem(){
        // set up network utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        // create JSONObject for uploading
        val jsonObject = JSONObject()
            .put(constants.ITEM_DESCRIPTION, item?.description)
            .put(constants.ITEM_DEPARTMENT, item?.department)
            .put(constants.ITEM_TERRITORY, item?.territory)
            .put(constants.ITEM_VENUE, item?.venue)
            .put(constants.ITEM_DATE, item?.date)
            .put(constants.ITEM_STATUS, item?.status)
        Log.d(constants.LOG_TAG, jsonObject.toString())

        // create relevant PUT request to update item
        val request = JsonObjectRequest(
            Request.Method.PUT, url + "/" + item?._id, jsonObject,
            { response ->
                Log.d(constants.LOG_TAG, response.toString())
                //TODO: parse the response here (or in the API) for DB errors
                getSnackbar(constants.SUCCESS_ITEM_UPLOAD, colorPrimary).show()

                // exit the fragment after saving item
                exitFragment()
            },
            { err ->
                Log.d(constants.LOG_TAG, err.toString())
                getSnackbar(constants.ERROR_ITEM_UPLOAD, colorDanger).show()
            }
        )

        // send request
        requestQueue.add(request)
    }

    private fun deleteItem() {
        // set up network utilities
        val cache = DiskBasedCache(activity?.cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack(null, null))
        val requestQueue = RequestQueue(cache, network).apply { start() }

        // create relevant PUT request to update item
        val request = JsonObjectRequest(
            Request.Method.DELETE, url + "/" + item?._id, null,
            { response ->
                Log.d(constants.LOG_TAG, response.toString())
                //TODO: parse the response here (or in the API) for DB errors

                //delete from Realm
                realm.beginTransaction()
                item?.deleteFromRealm()
                realm.commitTransaction()

                getSnackbar(constants.SUCCESS_ITEM_DELETE, colorDanger).show()

                // exit fragment
                exitFragment()
            },
            { err ->
                Log.d(constants.LOG_TAG, err.toString())

                getSnackbar(constants.ERROR_ITEM_DELETE, colorDanger).show()
            }
        )

        // send request
        requestQueue.add(request)
    }

    /**
     * Exit the fragment after deleting or saving an item
     * **/
    private fun exitFragment() {
        binding.closeItemEdit.performClick()
    }

    private fun getSnackbar(msg: String, snackColor: Int) : Snackbar {
        val sBar = Snackbar.make(
            activity?.findViewById(R.id.fragment)!!,
            msg,
            Snackbar.LENGTH_LONG
        )
        sBar.view.setBackgroundColor(snackColor)
        return sBar
    }
}
