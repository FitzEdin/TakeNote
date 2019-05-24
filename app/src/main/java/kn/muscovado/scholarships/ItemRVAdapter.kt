package kn.muscovado.scholarships


import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.scholarships.content.Item
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Adapter for RecyclerView w/list of items
 */
class ItemRVAdapter
    : RecyclerView.Adapter<ItemRVAdapter.ViewHolder>() {

    private var realm = Realm.getDefaultInstance()
    // list of items
    private var items = realm.where<Item>().findAll()
    private val openURL = Intent(Intent.ACTION_VIEW)

//    private var bundle = Bundle()
//    private var constants = Constants()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemRVAdapter.ViewHolder, position: Int) {

        // assign item info to views
        holder.mTitleView.text = items[position]?.title
        holder.mCoverageView.text = items[position]?.coverage
        holder.mLevelView.text = items[position]?.level
        holder.mProgrammeView.text = items[position]?.programme
        holder.mLocationView.text = items[position]?.location
        holder.mOpenToView.text = items[position]?.open_to

        val ctx = holder.itemView.context

        // open URL on click
        val mOnClickListener = View.OnClickListener {
            Log.d("Move", "Item Number " + items?.get(position)?.link)

            openURL.data = Uri.parse(items?.get(position)?.link)
            ctx.startActivity(openURL)
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
        val mProgrammeView: TextView = mView.item_programme
        val mLocationView: TextView = mView.item_location
        val mOpenToView: TextView = mView.item_open_to

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
