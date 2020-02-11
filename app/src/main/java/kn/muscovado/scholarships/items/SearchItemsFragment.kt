package kn.muscovado.scholarships.items


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
import kn.muscovado.scholarships.R
import kn.muscovado.scholarships.content.Item
import kn.muscovado.scholarships.utils.Constants
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
            = realm.where<Item>().contains("title", "nothin", Case.INSENSITIVE).findAll()

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
            .contains(constants.ITEM_TITLE, key, Case.INSENSITIVE)
            .or()
            .contains(constants.ITEM_COVERAGE, key, Case.INSENSITIVE)
            .or()
            .contains(constants.ITEM_LEVEL, key, Case.INSENSITIVE)
            .or()
            .contains(constants.ITEM_PROGRAMME, key, Case.INSENSITIVE)
            .or()
            .contains(constants.ITEM_LOCATION, key, Case.INSENSITIVE)
            .or()
            .contains(constants.ITEM_OPEN_TO, key, Case.INSENSITIVE)
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

            holder.mTitleView.text = items?.get(position)?.title
            holder.mCoverageView.text = items?.get(position)?.coverage + " Funding"
            holder.mLevelView.text = items?.get(position)?.level + " Degree"
//            holder.mProgrammeView.text = items?.get(position)?.programme
//            holder.mLocationView.text = items?.get(position)?.location
            holder.mOpenToView.text = items?.get(position)?.open_to
            holder.mInitialView.text = holder.mTitleView.text.first().toString()

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, constants.LOG_MSG_ITEM_LINK + items?.get(position)?.link)

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
            val mTitleView: TextView = mView.item_title
            val mCoverageView: TextView = mView.item_coverage
            val mLevelView: TextView = mView.item_level
//            val mProgrammeView: TextView = mView.item_programme
//            val mLocationView: TextView = mView.item_location
            val mOpenToView: TextView = mView.item_open_to
            val mInitialView: TextView = mView.item_initial

            override fun toString(): String {
                return super.toString() + " '" + mTitleView.text + "'"
            }
        }
    }
}
