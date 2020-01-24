package kn.muscovado.scholarships


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_edit_list.*

/**
 * Shows the two lists of scholarships, those vetted and those needing to be edited
 */
class EditListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // back button
        back_edit_list.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_editListFrag_pop)
        )
        // to edit item
        search_edit_list.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_editItemFrag)
        )
    }


}
