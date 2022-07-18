package com.example.kidssecurity.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.FragmentUserProfileBinding
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.view_model.user_profile.UserProfileViewModel
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent

class UserProfileFragment : Fragment() {

    // Binding object instance corresponding to the fragment_user_profile.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentUserProfileBinding? = null
    // Binding object instance corresponding to the fragment_login.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()

    private var isParent: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            //Get the the data sent to this fragment (the user data and the user type)
            isParent = it.getBoolean(IS_PARENT)
            val user = it.getParcelable<User>(USER)
            //If user not null
            user?.let{mUser ->
                viewModel.setUser(mUser)
                viewModel.setUserUri(mUser,isParent,requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.user.observe(viewLifecycleOwner){
            if (it != null){
               if (isParent){
                   //Cast the user data as [Parent]
                   val parent = it as Parent
                   //Set the parent data to the view to be displayed
                   binding.userProfileNameTextView.text = getString(R.string.name_string,parent.nameParent)
                   binding.phoneNumberTextView.text =
                       getString(R.string.phone_number_string_value,parent.telParent)
               }else{
                   //Cast the user data as [Child]
                   val child = it as Child
                   //Set the child data to the view to be displayed
                   binding.userProfileNameTextView.text = getString(R.string.name_string,child.nameChild)
                   binding.phoneNumberTextView.text =
                       getString(R.string.phone_number_string_value,child.telChild)
               }
            }
        }
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val IS_PARENT = "is_parent_profile"
        const val USER = "user_profile"
        const val TAG = "UserProfileFragment"
    }
}