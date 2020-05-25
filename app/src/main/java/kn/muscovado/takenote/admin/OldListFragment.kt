package kn.muscovado.takenote.admin


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.utils.Constants
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kotlinx.android.synthetic.main.fragment_old_list.*
import kotlinx.android.synthetic.main.item_old_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class OldListFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private var bundle = Bundle()
    private val constants = Constants()
    private var items
            = realm.where<Item>().equalTo(constants.ITEM_STATUS, constants.STATUS_VETTED).findAll()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (listView is RecyclerView) {
            with(listView) {
                layoutManager = LinearLayoutManager(context)
                adapter = OldListRVAdapter()
            }
        }

        if (items?.size!! > 0) {
            listView.visibility = View.VISIBLE
            empty_items_list.visibility = View.GONE
        }
        else {
            listView.visibility = View.GONE
            empty_items_list.visibility = View.VISIBLE
        }
    }

    inner class OldListRVAdapter
        : RecyclerView.Adapter<OldListRVAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_old_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.mTitleView.text = items[position]?.territory
            holder.mCoverageView.text = items[position]?.department
            holder.mProgrammeView.text = items[position]?.venue
            holder.mLocationView.text = items[position]?.description
            holder.mLevelView.text = items[position]?.date

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, constants.LOG_MSG_ITEM_LINK + items?.get(position)?.description)

                bundle.putString(constants.TAG_ITEM, items?.get(position)?._id)
                Navigation.findNavController(holder.mView).navigate(R.id.action_to_editItemFrag, bundle)
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
            val mProgrammeView: TextView = mView.item_programme
            val mLocationView: TextView = mView.item_location
            val mOpenToView: TextView = mView.item_open_to

            override fun toString(): String {
                return super.toString() + " '" + mTitleView.text + "'"
            }
        }
    }
}
