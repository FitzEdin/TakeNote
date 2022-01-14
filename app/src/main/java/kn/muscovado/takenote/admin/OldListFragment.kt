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
import kn.muscovado.takenote.databinding.FragmentOldListBinding
import kn.muscovado.takenote.databinding.ItemListBinding

/**
 * A simple [Fragment] subclass.
 */
class OldListFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private var bundle = Bundle()
    private val constants = Constants()
    private var items
            = realm.where<Item>()
        .equalTo(constants.ITEM_STATUS, constants.STATUS_VETTED)
        .findAll()

    private var _binding: FragmentOldListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOldListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.listView is RecyclerView) {
            with(binding.listView) {
                layoutManager = LinearLayoutManager(context)
                adapter = OldListRVAdapter()
            }
        }

        if (items?.size!! > 0) {
            binding.listView.visibility = View.VISIBLE
            binding.emptyItemsList.visibility = View.GONE
        }
        else {
            binding.listView.visibility = View.GONE
            binding.emptyItemsList.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class OldListRVAdapter
        : RecyclerView.Adapter<OldListRVAdapter.ViewHolder>() {

        private var _binding: ItemListBinding? = null
        private val innerBinding get() = _binding!!

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            _binding = ItemListBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(innerBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.mDescriptionView.text = items[position]?.description
            holder.mTerritoryView.text = items[position]?.territory
            holder.mDepartmentView.text = items[position]?.department
            holder.mDateView.text = items[position]?.date

            val mOnClickListener = View.OnClickListener {
                Log.d(constants.LOG_TAG, constants.LOG_MSG_ITEM_LINK + items?.get(position)?.description)

                bundle.putString(constants.TAG_ITEM, items?.get(position)?._id)
                Navigation.findNavController(innerBinding.root).navigate(R.id.action_to_editItemFrag, bundle)
            }

            with(innerBinding.root) {
                tag = items?.get(position)
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int = items?.size!!

        inner class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(innerBinding.root) {
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
