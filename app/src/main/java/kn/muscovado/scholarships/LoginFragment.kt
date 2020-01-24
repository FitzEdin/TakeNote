package kn.muscovado.scholarships


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up close button
        back_item_login.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFrag_pop)
        )

        // to edit item
        login_btn.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_to_editListFrag)
        )
    }


}
