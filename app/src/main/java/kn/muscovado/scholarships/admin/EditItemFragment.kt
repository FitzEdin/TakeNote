package kn.muscovado.scholarships.admin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import io.realm.Realm
import io.realm.kotlin.where
import kn.muscovado.scholarships.utils.Constants
import kn.muscovado.scholarships.R
import kn.muscovado.scholarships.content.Item
import kotlinx.android.synthetic.main.fragment_edit_item.*

/**
 * Used to edit an item in the list and save it to the DB.
 */
class EditItemFragment : Fragment() {
    private var constants = Constants()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_item, container, false)
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

        populateForm(item)

        // set up close button
        close_item_edit.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_editItemFrag_pop)
        )
    }

    private fun populateForm(item : Item?) {
        edit_item_title.setText(item?.title)
        edit_item_link.setText(item?.link)
        when(item?.coverage) {
            constants.FULL -> full_coverage_rbtn.isChecked = true
            else -> partial_coverage_rbtn.isChecked = true
        }

        when(item?.level) {
            constants.LEVEL_FOUR -> four_level_check.isChecked = true
            constants.LEVEL_THREE -> three_level_check.isChecked = true
            constants.LEVEL_TWO -> two_level_check.isChecked = true
            constants.LEVEL_ONE -> one_level_check.isChecked = true
            constants.LEVEL_ZERO -> zero_level_check.isChecked = true
        }

        edit_item_location.setText(item?.location)

        edit_item_programme.setText(item?.programme)

        edit_item_open_to.setText(item?.open_to)

        edit_item_status.isChecked = when(item?.status) {
            constants.STATUS_VETTED -> true
            else -> false
        }
    }

}
