package kn.muscovado.takenote.items


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.utils.Constants
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kotlinx.android.synthetic.main.fragment_item_details.*

/**
 * Displays the details for the listed items
 */
class ItemDetailsFragment : Fragment() {
    private var constants = Constants()
//    private val openURL = Intent(Intent.ACTION_VIEW)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id =
            if (arguments?.getString(constants.TAG_ITEM) != null)
                arguments?.getString(constants.TAG_ITEM)!!
            else throw NullPointerException("Expression 'arguments?.getInt(lists.TAG_ITEM)' must not be null")

        val realm = Realm.getDefaultInstance()
        val item = realm.where<Item>()
            .equalTo(constants.ITEM_ID, id)
            .findFirst()


        val shareItem = Intent()
            .setAction(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_TEXT, constants.SHARE_EXTRA_START +
                    "${item?.territory} for ${item?.description}" +
                    constants.SHARE_EXTRA_END + constants.SHARE_EXTRA_LINK)
            .setType(constants.TEXT_PLAIN)

        // open URL on click
//        val mOnClickListener = View.OnClickListener {
////            openURL.data = Uri.parse(item?.link)
//            startActivity(openURL)
//        }
        // share item
        val mOnShareClickListener = View.OnClickListener {
            startActivity(Intent.createChooser(shareItem, constants.SHARE_TITLE))
        }

        // populate view
        item_details_territory.text = item?.territory
        item_details_department.text = item?.department
        item_details_date.text = item?.date
        item_details_description.text = item?.description
        item_details_venue.text = item?.venue

        back_item_details.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_itemDetailsFrag_pop)
        )
        share_item_details.setOnClickListener(mOnShareClickListener)
    }
}
