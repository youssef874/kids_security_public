package com.example.kidssecurity.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.FragmentLoadingBinding
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.view_model.loading.LoadingViewModel
import com.example.kidssecurity.view_model.loading.LoadingViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Tis class is a ui controller to The loading view
 */
class LoadingFragment : Fragment() {

    // Binding object instance corresponding to the fragment_loading.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentLoadingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val retrofitRepository = Repository.retrofitRepository

    private val factory = LoadingViewModelFactory(retrofitRepository)

    private val viewModel: LoadingViewModel by viewModels { factory }

    //This variable to define user type(Parent=true) or (Child=false)
    private var isUserParent: Boolean? = null

    //This variable Hold the [Parent] data if the user is parent
    private var parent: Parent? = null

    //This variable Hold the [Child] data if the user is child if user login as child
    private var child: Child? = null

    //This variable will hold the user parent data if the user is child if user login as child
    private var childParent: Parent? = null

    //This will hold the location permission result
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var permissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserParent = it.getBoolean(IS_PARENT_USER)
            if (isUserParent as Boolean) {
                parent = it.getParcelable(USER)
            } else {
                child = it.getParcelable(USER)
            }
        }
        (activity as AppCompatActivity).supportActionBar?.hide()
        if (parent != null) {
            viewModel.downloadAllImageForParentUser(parent!!, requireContext())
        }
        if (child != null) {
            viewModel.getChildParent(child!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
               permissionGranted = isGranted
            }

        viewModel.childParent.observe(viewLifecycleOwner) {
            if (it != null) {
                requestPermission()
                if (it.data != null && it.data != Parent()){
                    if (permissionGranted){
                        childParent = it.data
                        viewModel.downloadAllImagesForChildUser(child!!, requireContext(), it.data)
                    }
                    else{
                        displayWarningDialog()
                    }

                }
            }
        }

        return binding.root
    }

    /**
     * This function responsible for asking for access location user permission
     */
    private fun requestPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    CreateAccountFragment.permissions[0]
                ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            it,
                            CreateAccountFragment.permissions[1]
                        ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch(CreateAccountFragment.permissions[0])
                    requestPermissionLauncher.launch(CreateAccountFragment.permissions[1])
                }else ->{
                    permissionGranted =true
                }

            }
        }
    }

    /**
     * This function responsible for inform the user [Child] the the permission
     * is require for the app to run
     */
    private fun displayWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.location_permission_warning_title))
            .setMessage(getString(R.string.location_permission_warning_body))
            .setPositiveButton(getString(R.string.location_permission_warning_positive)){_,_ ->
                requestPermission()
            }
            .setNegativeButton(getString(R.string.location_permission_warning_negative)){_,_ ->
                requireActivity().finish()
            }.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.isParentUserImageAdded.observe(viewLifecycleOwner) {
            if (it != null) {
                //If the the images related to the parent has been downloaded navigate to the ParentHomeFragment
                if (it)
                    parent?.let { it1 -> navigateToParentHomeFragment(it1) }
            }
        }

        viewModel.isChildUserRelatedImagesAdded.observe(viewLifecycleOwner){
            if (it != null){
                if (it){
                    navigateToChildHomeFragment(childParent!!,child!!)
                }
            }
        }
    }

    private fun navigateToChildHomeFragment(childParent: Parent, child: Child) {
        val action = LoadingFragmentDirections.actionLoadingFragmentToChildHomeFragment(child, childParent)
        findNavController().navigate(action)
    }

    /**
     * This function responsible on navigating to [ParentHomeFragment]
     * @param parent: the parent data that will be transferred to the destination
     */
    private fun navigateToParentHomeFragment(parent: Parent) {
        val action = LoadingFragmentDirections.actionLoadingFragmentToParentHomeFragment(parent)
        findNavController().navigate(action)
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
        const val IS_PARENT_USER = "is_user_parent"
        const val USER = "user"
        const val TAG = "LoadingFragment"
        const val PARENT = "parent"
        val permissions = arrayOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )
    }
}