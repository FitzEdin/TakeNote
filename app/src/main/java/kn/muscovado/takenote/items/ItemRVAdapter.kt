package kn.muscovado.takenote.items


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.utils.Constants
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kotlinx.android.synthetic.main.item_list.view.*

/**
 * Adapter for RecyclerView w/list of items
 */
class ItemRVAdapter
    : RecyclerView.Adapter<ItemRVAdapter.ViewHolder>() {

    private var bundle = Bundle()
    private var constants = Constants()

    private var realm = Realm.getDefaultInstance()
    // list of items
    private var items
            = realm.where<Item>()
        .equalTo(constants.ITEM_STATUS, constants.STATUS_VETTED)
        .findAll()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // assign item info to views
//        holder.mTitleView.text = items[position]?.title
//        holder.mCoverageView.text = items[position]?.coverage
//        holder.mLevelView.text = items[position]?.level
//        holder.mProgrammeView.text = items[position]?.programme
//        holder.mLocationView.text = items[position]?.location
//        holder.mOpenToView.text = items[position]?.open_to
        Log.d(constants.LOG_TAG, "Item Number " + items[position]?._id)
        Log.d(constants.LOG_TAG, "Item Number " + items[position]?.status)

        // navigate to details fragment
        val mOnClickListener = View.OnClickListener {
            Log.d(constants.LOG_TAG, "Item Number " + items[position]?._id)
            bundle.putString(constants.TAG_ITEM, items[position]?._id!!)
            Navigation.findNavController(holder.mView).navigate(R.id.action_to_itemDetailsFrag, bundle)
        }

        with(holder.mView) {
            tag = items[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        // get handle on views
        val mTitleView: TextView = mView.item_title
        val mCoverageView: TextView = mView.item_coverage
        val mLevelView: TextView = mView.item_level
//        val mProgrammeView: TextView = mView.item_programme
//        val mLocationView: TextView = mView.item_location
        val mOpenToView: TextView = mView.item_open_to

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
