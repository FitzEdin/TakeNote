package kn.muscovado.takenote.admin


import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kn.muscovado.takenote.R
import kn.muscovado.takenote.databinding.FragmentLoginBinding
import kn.muscovado.takenote.databinding.FragmentSearchItemsBinding

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up close button
        binding.backItemLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFrag_pop)
        )

        // to edit item
        binding.loginBtn.setOnClickListener{login()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun login(){
        // get PIN entered
        val pin = binding.loginText.text.toString()
        // check validity
        if (pin == "1869") {
            //      go to editListFrag
            this.findNavController().navigate(R.id.action_to_editListFrag)
        } else {
            //      else let user know
            val tEmpty = Toast.makeText(this.requireContext(), "Bob dropped the ball", Toast.LENGTH_LONG)
            tEmpty.setGravity(Gravity.CENTER, 0,0)
            tEmpty.show()
        }
    }


}
