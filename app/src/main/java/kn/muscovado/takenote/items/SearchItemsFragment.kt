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
import kn.muscovado.takenote.utils.Constants
import kotlinx.android.synthetic.main.fragment_search_items.*
import kotlinx.android.synthetic.main.item_list.view.*

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (searchListView is RecyclerView) {
            with(searchListView) {
                layoutManager = LinearLayoutManager(context)
                adapter = SearchRVAdapter()
            }
        }

        back_items_search.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_searchItemsFrag_pop)
        )

        clear_search_items.setOnClickListener { search_items_text.text.clear() }
        clear_search_items.visibility = View.INVISIBLE

        search_items_text.addTextChangedListener(searchTextWatcher)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!search_items_text.text.isEmpty()) {
                clear_search_items.visibility = View.VISIBLE
                searchFor(search_items_text.text.toString().toLowerCase())
            } else {
                clear_search_items.visibility = View.INVISIBLE
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

        searchListView.adapter?.notifyDataSetChanged()
        search_amount.text = String.format("%d", count)
    }

    inner class SearchRVAdapter
        : RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.mDescriptionView.text = items?.get(position)?.description
            holder.mTerritoryView.text = items?.get(position)?.territory
            holder.mDepartmentView.text = items?.get(position)?.department
            holder.mDateView.text = items?.get(position)?.date

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, constants.LOG_MSG_ITEM_LINK + items?.get(position)?.description)

                bundle.putString(constants.TAG_ITEM, items?.get(position)?._id)
                Navigation.findNavController(holder.mView).navigate(R.id.action_to_itemDetailsFrag, bundle)
            }

            with(holder.mView) {
                tag = items?.get(position)
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int = items?.size!!

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mDescriptionView: TextView = mView.item_description
            val mTerritoryView: TextView = mView.item_territory
            val mDepartmentView: TextView = mView.item_department
            val mDateView: TextView = mView.item_date

            override fun toString(): String {
                return super.toString() + " '" + mDescriptionView.text + "'"
            }
        }
    }
}
