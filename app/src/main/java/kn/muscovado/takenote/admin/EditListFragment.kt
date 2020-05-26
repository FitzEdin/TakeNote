package kn.muscovado.takenote.admin


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.Navigation
import kn.muscovado.takenote.utils.Constants
import kn.muscovado.takenote.R
import kotlinx.android.synthetic.main.fragment_edit_list.*

/**
 * Shows the two lists of scholarships, those vetted and those needing to be edited
 */
class EditListFragment : Fragment() {

    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

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
        search_edit_list.setOnClickListener{}



        sectionsPagerAdapter =
            SectionsPagerAdapter(
                childFragmentManager
            )
        pager.adapter = sectionsPagerAdapter

        tab_layout.setupWithViewPager(pager)
    }

    class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val constants = Constants()

        override fun getCount(): Int = 2

        override fun getItem(i: Int): Fragment {
            return when(i) {
                0 -> NewListFragment()
                else -> OldListFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position){
                0 -> constants.TITLE_NEW
                else -> constants.TITLE_OLD
            }
        }
    }

}
