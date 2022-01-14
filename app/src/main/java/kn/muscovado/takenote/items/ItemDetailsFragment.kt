package kn.muscovado.takenote.items


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.takenote.R
import kn.muscovado.takenote.content.Item
import kn.muscovado.takenote.databinding.FragmentItemDetailsBinding
import kn.muscovado.takenote.utils.Constants

/**
 * Displays the details for the listed items
 */
class ItemDetailsFragment : Fragment() {
    private var constants = Constants()
//    private val openURL = Intent(Intent.ACTION_VIEW)

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
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
        binding.itemDetailsTerritory.text = item?.territory
        binding.itemDetailsDepartment.text = item?.department
        binding.itemDetailsDate.text = item?.date
        binding.itemDetailsDescription.text = item?.description
        binding.itemDetailsVenue.text = item?.venue

        binding.backItemDetails.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_itemDetailsFrag_pop)
        )
        binding.shareItemDetails.setOnClickListener(mOnShareClickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
