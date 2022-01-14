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
import kn.muscovado.takenote.databinding.ItemListBinding

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

    private var _binding: ItemListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // assign item info to views
        holder.mTitleView.text = items[position]?.description
        holder.mCoverageView.text = items[position]?.territory
        holder.mLevelView.text = items[position]?.department
        holder.mOpenToView.text = items[position]?.date
        Log.d(constants.LOG_TAG, "Item Number " + items[position]?._id)
        Log.d(constants.LOG_TAG, "Item Number " + items[position]?.status)

        // navigate to details fragment
        val mOnClickListener = View.OnClickListener {
            Log.d(constants.LOG_TAG, "Item Number " + items[position]?._id)
            bundle.putString(constants.TAG_ITEM, items[position]?._id!!)
            Navigation.findNavController(binding.root).navigate(R.id.action_to_itemDetailsFrag, bundle)
        }

        with(binding.root) {
            tag = items[position]
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        // get handle on views
        val mCoverageView: TextView = binding.itemTerritory
        val mLevelView: TextView = binding.itemDepartment
        val mTitleView: TextView = binding.itemDescription
        val mOpenToView: TextView = binding.itemDate

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
