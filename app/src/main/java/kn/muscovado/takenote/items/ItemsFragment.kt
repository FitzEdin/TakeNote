package kn.muscovado.takenote.items


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.utils.Constants
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kn.muscovado.takenote.databinding.FragmentItemsBinding

class ItemsFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private val constants = Constants()

    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchItems.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_searchItemsFrag)
        )
        binding.newItem.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_addItemFrag)
        )
        binding.unlock.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_loginFrag)
        )

        if (binding.listView is RecyclerView) {
            with(binding.listView) {
                layoutManager = LinearLayoutManager(context)
                adapter = ItemRVAdapter()
            }
        }

        if (realm.where<Item>()
                .equalTo(constants.ITEM_STATUS, constants.STATUS_VETTED)
                .findAll().size > 0) {
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
}
