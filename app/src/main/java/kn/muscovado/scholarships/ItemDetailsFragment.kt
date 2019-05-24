package kn.muscovado.scholarships


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.scholarships.content.Item
import kotlinx.android.synthetic.main.fragment_item_details.*

/**
 * Displays the details for the listed items
 */
class ItemDetailsFragment : Fragment() {
    private var constants = Constants()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id
                = if (arguments?.getString(constants.TAG_ITEM) != null)
            arguments?.getInt(constants.TAG_ITEM)!!
        else throw NullPointerException("Expression 'arguments?.getInt(lists.TAG_ITEM)' must not be null")

        val realm = Realm.getDefaultInstance()
        val item = realm.where<Item>()
            .equalTo(constants.ITEM_ID, id)
            .findFirst()

        // populate view
        item_title.text = item?.title
//        item_id.text = item?.number.toString()
//        item_details.text = item?.content

        back_item_details.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_itemDetailsFrag_pop)
        )
    }
}
