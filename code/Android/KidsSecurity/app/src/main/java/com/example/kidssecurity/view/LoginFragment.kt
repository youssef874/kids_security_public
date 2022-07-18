package com.example.kidssecurity.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.FragmentLoginBinding
import com.example.kidssecurity.hideKeyboard
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.setError
import com.example.kidssecurity.view_model.login.LoginViewModel
import com.example.kidssecurity.view_model.login.LoginViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent

/**
 * Tis class is a ui controller to The login view
 */
class LoginFragment : Fragment() {

    // Binding object instance corresponding to the fragment_login.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //This property represent the data from retrofit
    private val retrofitRepository = Repository.retrofitRepository

    //This property is a factory for LoginViewModel
    private val factory = LoginViewModelFactory(retrofitRepository)

    private val viewModel: LoginViewModel by viewModels { factory }

    //This variable to identify the user if it is parent or not
    private var isParent = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //When the create button clicked
        binding.createButton.setOnClickListener {
            navigateToCreateAccountFragment()
        }

        //Handle the change between parent and child radio button
        binding.userChoice.setOnCheckedChangeListener { radioGroup, checkedId ->
            changeUser(checkedId)
        }

        //Listen to user changes through this fragment life cycle with the architecture component help
        viewModel.isUserParent.observe(viewLifecycleOwner) {
            createButtonVisibility(it)
            this.isParent = it
        }

        //Listen to parent changes through this fragment life cycle if the user identify himself
        //as parent and he is authenticated to use the app
        viewModel.parent.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data != null && it.data != Parent()) {
                    disableTextFieldError()
                    navigateToLoadingFragment(true, it.data)
                } else if (it.data == Parent()) {
                    if (isParent) {
                        disableLoading()
                        wrongInput()
                    }
                }
            }
        }

        //Listen to child changes through this fragment life cycle if the user identify himself
        //as child and he is authenticated to use the app
        viewModel.child.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.data != null && it.data != Child()) {
                    disableTextFieldError()
                    navigateToLoadingFragment(false, it.data)
                } else if (it.data == Child()) {
                    if (!isParent) {
                        disableLoading()
                        wrongInput()
                    }
                }
            }
        }

        //When the login button clicked
        binding.signInButton.setOnClickListener {
            enableLoading()
            getUserInformation()
            disableTextFieldError()
        }

        binding.loginLayoutContainer.setOnClickListener {
            disableTextFieldError()
            hideKeyboard()
        }
    }

    /**
     * This function will enable circular progress bar
     * and disable both button to inform user that the authentication
     * is in being verified
     */
    private fun enableLoading() {
        binding.loginLoading.visibility = View.VISIBLE
        binding.signInButton.visibility = View.INVISIBLE
        binding.createButton.visibility = View.INVISIBLE
    }

    /**
     * This function will disable circular progress bar
     * and enable both button to inform user that the authentication
     * is not verified
     */
    private fun disableLoading() {
        binding.loginLoading.visibility = View.INVISIBLE
        if (isParent)
            binding.createButton.visibility = View.VISIBLE
        binding.signInButton.visibility = View.VISIBLE

    }

    /**
     * get the user login and password from the ui
     * and call getAuthenticatedParent() function passing the
     * information as parameters if user is parent or call getAuthenticatedChild
     * otherwise
     */
    private fun getUserInformation() {
        val name = binding.loginEditText.text.toString()
        val psw = binding.passwordEditText.text.toString()
        if (name.isEmpty()) {
            disableLoading()
            binding.loginTextField.setError(true, getString(R.string.empty_field_error))
        }
        if (psw.isEmpty()) {
            disableLoading()
            binding.passwordTextField.setError(true, getString(R.string.empty_field_error))
        }
        if (this.isParent) {
            viewModel.getAuthenticatedParent(name, psw)
        } else {
            viewModel.getAuthenticatedChild(name, psw)
        }
    }

    /**
     * Navigate to the loading ui passing the user data and type
     * @param isUserParent: The user status(parent or not)
     * @param user: the user data
     */
    private fun navigateToLoadingFragment(isUserParent: Boolean, user: User) {
        disableLoading()
        val action = LoginFragmentDirections.actionLoginFragmentToLoadingFragment(
            isUserParent,
            user
        )
        findNavController().navigate(action)
    }

    /**
     * Navigate to the create account ui passing the user type
     */
    private fun navigateToCreateAccountFragment() {
        disableLoading()
        val action = LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment(true, null)
        findNavController().navigate(action)
    }

    /**
     * Control the create button visibility depending whether
     * the user is parent or not
     * @param isUserParent: The user is parent or not
     */
    private fun createButtonVisibility(isUserParent: Boolean) {
        if (isUserParent) {
            binding.createButton.visibility = View.VISIBLE
        } else {
            binding.createButton.visibility = View.INVISIBLE
        }
    }

    /**
     * Change the user type in the corresponding view model by calling
     * changeUser()
     * @param checkedId :The radio button id of either parent or child radio button
     */
    private fun changeUser(checkedId: Int) {
        if (checkedId == R.id.parent_radio_button) {
            viewModel.changeUser(true)
        } else {
            viewModel.changeUser(false)
        }
    }

    /**
     * This function to disable the error state of Text fields
     */
    private fun disableTextFieldError() {
        binding.loginTextField.setError(false)
        binding.passwordTextField.setError(false)
    }

    /**
     * This function will enable the error state of Text fields
     * with correspond message
     */
    private fun wrongInput() {
        binding.loginTextField.setError(true, getString(R.string.wrong_Input_error))
        binding.passwordTextField.setError(true, getString(R.string.wrong_Input_error))
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
        const val TAG = "LoginFragment"
        const val IS_PARENT = "isParent"
    }
}