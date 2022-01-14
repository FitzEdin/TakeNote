package kn.muscovado.takenote.items


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kn.muscovado.takenote.databinding.FragmentSearchItemsBinding
import kn.muscovado.takenote.databinding.ItemListBinding
import kn.muscovado.takenote.utils.Constants

/**
 * Handles searching and displays the results
 */
class SearchItemsFragment : Fragment() {

    private val constants = Constants()
    private var bundle = Bundle()

    private var realm = Realm.getDefaultInstance()
    // items matching search query
    private var items: RealmResults<Item>?
            = realm.where<Item>().contains(constants.ITEM_DESCRIPTION, "nothin", Case.INSENSITIVE).findAll()

    private var _binding: FragmentSearchItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchItemsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.searchListView is RecyclerView) {
            with(binding.searchListView) {
                layoutManager = LinearLayoutManager(context)
                adapter = SearchRVAdapter()
            }
        }

        binding.backItemsSearch.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_searchItemsFrag_pop)
        )

        binding.clearSearchItems.setOnClickListener { binding.searchItemsText.text.clear() }
        binding.clearSearchItems.visibility = View.INVISIBLE

        binding.searchItemsText.addTextChangedListener(searchTextWatcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!binding.searchItemsText.text.isEmpty()) {
                binding.clearSearchItems.visibility = View.VISIBLE
                searchFor(binding.searchItemsText.text.toString().lowercase())
            } else {
                binding.clearSearchItems.visibility = View.INVISIBLE
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    /**
     * Perform a search for the key provided; the fragment is swapped in
     * if not existing previously. Each time it is handed a new key.
     * @param key
     */
    fun searchFor(key: String) {
        items = realm.where<Item>()
            .contains(constants.ITEM_STATUS, constants.STATUS_VETTED, Case.INSENSITIVE)
            .and()
            .beginGroup()
                .contains(constants.ITEM_TERRITORY, key, Case.INSENSITIVE)
                .or()
                .contains(constants.ITEM_DEPARTMENT, key, Case.INSENSITIVE)
                .or()
                .contains(constants.ITEM_DATE, key, Case.INSENSITIVE)
                .or()
                .contains(constants.ITEM_VENUE, key, Case.INSENSITIVE)
                .or()
                .contains(constants.ITEM_DESCRIPTION, key, Case.INSENSITIVE)
            .endGroup()
            .distinct(constants.ITEM_ID)?.findAll()
        // update count
        val count = items?.size

        // update list only if something was found,
        // otherwise keep last set of valid search results
        //        if(count > 0){  adapter.notifyDataSetChanged(); }

        binding.searchListView.adapter?.notifyDataSetChanged()
        binding.searchAmount.text = String.format("%d", count)
    }

    inner class SearchRVAdapter
        : RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {

        private var _binding: ItemListBinding? = null
        private val binding get() = _binding!!

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            _binding = ItemListBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.mDescriptionView.text = items?.get(position)?.description
            holder.mTerritoryView.text = items?.get(position)?.territory
            holder.mDepartmentView.text = items?.get(position)?.department
            holder.mDateView.text = items?.get(position)?.date

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, constants.LOG_MSG_ITEM_LINK + items?.get(position)?.description)

                bundle.putString(constants.TAG_ITEM, items?.get(position)?._id)
                Navigation.findNavController(binding.root).navigate(R.id.action_to_itemDetailsFrag, bundle)
            }

            with(binding.root) {
                tag = items?.get(position)
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int = items?.size!!

        inner class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
            val mDescriptionView: TextView = binding.itemDescription
            val mTerritoryView: TextView = binding.itemTerritory
            val mDepartmentView: TextView = binding.itemDepartment
            val mDateView: TextView = binding.itemDate

            override fun toString(): String {
                return super.toString() + " '" + mDescriptionView.text + "'"
            }
        }
    }
}
