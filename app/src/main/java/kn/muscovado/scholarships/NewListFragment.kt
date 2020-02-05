package kn.muscovado.scholarships


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
import io.realm.RealmResults
import io.realm.kotlin.where
import kn.muscovado.scholarships.content.Item
import kotlinx.android.synthetic.main.fragment_new_list.*
import kotlinx.android.synthetic.main.item_new_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class NewListFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private var bundle = Bundle()
    private val constants = Constants()
    private var items: RealmResults<Item>?
            = realm.where<Item>().equalTo(constants.ITEM_STATUS, constants.STATUS_NEW).findAll()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_list, container, false)
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.mTitleView.text = items?.get(position)?.title
            holder.mLinkView.text = items?.get(position)?.link

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, "Item Number " + items?.get(position)?.link)

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
            val mLinkView: TextView = mView.item_link

            override fun toString(): String {
                return super.toString() + " '" + mTitleView.text + "'"
            }
        }
    }
}
