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
import kotlinx.android.synthetic.main.fragment_items.*

class ItemsFragment : Fragment() {

    private var realm = Realm.getDefaultInstance()
    private val constants = Constants()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_items.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_searchItemsFrag)
        )
        new_item.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_addItemFrag)
        )
        unlock.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_loginFrag)
        )

        if (listView is RecyclerView) {
            with(listView) {
                layoutManager = LinearLayoutManager(context)
                adapter = ItemRVAdapter()
            }
        }

        if (realm.where<Item>()
                .equalTo(constants.ITEM_STATUS, constants.STATUS_VETTED)
                .findAll().size > 0) {
            listView.visibility = View.VISIBLE
            empty_items_list.visibility = View.GONE
        }
        else {
            listView.visibility = View.GONE
            empty_items_list.visibility = View.VISIBLE
        }
    }
}
