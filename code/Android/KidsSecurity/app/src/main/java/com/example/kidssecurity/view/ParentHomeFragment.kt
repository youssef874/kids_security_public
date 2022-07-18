package com.example.kidssecurity.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.FragmentParentHomeBinding
import com.example.kidssecurity.model.ChildListItem
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.view.adapter.ChildListAdapter
import com.example.kidssecurity.view_model.parent_home.ParentHomeViewModel
import com.example.kidssecurity.view_model.parent_home.ParentHomeViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ParentHomeFragment : Fragment(), OnMapReadyCallback {

    // Binding object instance corresponding to the fragment_parent_home.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentParentHomeBinding? = null

    // Binding object instance corresponding to the fragment_login.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private val binding get() = _binding!!

    private val retrofitRepository = Repository.retrofitRepository

    private val factory = ParentHomeViewModelFactory(retrofitRepository)

    private val viewModel: ParentHomeViewModel by viewModels { factory }

    //This property will hold the call permission result
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //This property will hold the parent user
    private lateinit var parent: Parent

    private var mMap: GoogleMap? = null

    //This property will hold the selected child data
    private var currentChild: Child? = null
    private var lastChild: Child? = null

    //This property will be true if the user data that will be displayed in the profile is parent
    //otherwise will be false
    private var isUserProfileParent: Boolean? = null

    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get the parent data sent from Loading fragment
        arguments?.let {
            parent = it.getParcelable<Parent>(LoadingFragment.PARENT)!!
        }
        //Enable option menu
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        viewModel.mapAllChildrenImageToTheirUri(parent, requireContext())
        if (parent.children.isNotEmpty()) {
            lastChild = parent.children[0]
            viewModel.postTheCurrentChild(parent.children[0])
        }
        //Control the back stack
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //Pop up back stack if there any thing left
                if (!findNavController().popBackStack()) {
                    //Close the app
                    activity?.finish()
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentParentHomeBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        //bind the xml layout lifecycle to this fragment lifecycle
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Get the map fragment from the layout
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.recyclerView.adapter =
            ChildListAdapter(requireContext()) { childListItem: ChildListItem ->
                //Call postTheCurrentChild method passing the selected children form the adapter
                viewModel.postTheCurrentChild(childListItem.child!!)
            }

        //When profile floating button is clicked
        binding.childProfileFloatingButton.setOnClickListener {
            //If there is a selected child
            if (currentChild != null) {
                //Specify the user as child to be display it is profile
                isUserProfileParent = false
                viewModel.displayUserProfile(currentChild!!)
            }
        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) { isGranted ->
            //If the call permission is granted
            if (!isGranted) {
                displayWarningDialog()
            } else {
                makeCall(currentChild!!)
            }

        }

        //When the call floating button clicked
        binding.callParentHomeFb.setOnClickListener {
            call()
        }

        //When the chat floating button clicked
        binding.chatParentHomeFb.setOnClickListener {
            navigateToChatFragment()
            if (parent.children.isNotEmpty())
                viewModel.displayChatComplete(parent.children[0])
        }

        //Observe navigateToUserProfile property of  ParentHomeViewModel changes,how is going to
        //hold the user data to display in the UserProfileFragment
        viewModel.navigateToUserProfile.observe(viewLifecycleOwner) {
            if (it != null) {
                //Null check of isUserProfileParent property if it is not null navigateToUserProfile passing
                //it as parameter with the user data
                isUserProfileParent?.let { it1 -> navigateToUserProfile(it1, it) }
                //Calling this method to reset navigateToUserProfile so it wont display the wrong user data
                //when navigating to and from UserProfileFragment multiple time
                if (parent.children.isNotEmpty())
                    viewModel.displayProfileComplete(parent.children[0])
            }
        }

        //Observe currentChild property changes
        viewModel.currentChild.observe(viewLifecycleOwner) {
            if (it != null) {
                lastChild = currentChild
                lastChild?.isTrackable = false
                lastChild?.let { it1 -> viewModel.getCurrentLocation(it1) }
                currentChild = it
                Log.i(TAG, "child:${it.idChild}")
                Log.i(TAG,"last child ${lastChild?.idChild}")
                viewModel.getCurrentLocation(it)
                val location = it.location
                if (location.isEmpty()) {
                    mMap?.clear()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.no_location),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.selectedChildCurrentLocation.observe(viewLifecycleOwner) {
            if (it != null) {
                    mMap?.let { map ->
                        displayChildLocation(
                            LatLng(
                                it.latLocation.toDouble(),
                                it.longLocation.toDouble()
                            ), map
                        )
                    }
            }
        }

    }

    /**
     * This function will navigate to [ChatFragment] passing the parent
     * and the current selected child and the type of type sender (parent or not)
     * as data to the destination
     */
    private fun navigateToChatFragment() {
        val action = currentChild?.let {
            ParentHomeFragmentDirections.actionParentHomeFragmentToChatFragment(
                sender = parent,
                receiver = it,
                isSenderParent = true
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    /**
     * This function will be called when the call floating button pressed
     */
    private fun call() {
        requestPermission()
        displayWarningDialog()
    }

    /**
     * This function will display a waring dialog to the parent user
     * to inform him that he is going to call his child who is displayed in
     * the message with his phone number and ask for conformation for make the phone call
     */
    private fun displayWarningDialog() {
        currentChild?.let {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.call_warning_dialog_title)
                .setMessage(
                    getString(R.string.call_warning_dialog_body, it.telChild, it.nameChild)
                )
                .setPositiveButton(R.string.call_dialog_positive_button) { _, _ ->
                    makeCall(it)
                }
                .show()
        }
    }

    /**
     * This function will be responsible in making the phone call to
     * the child
     * @param child:the child who is going to make phone call to
     */
    private fun makeCall(child: Child) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:${child.telChild}")
        startActivity(intent)

    }

    /**
     * This function will ask the user for phone call permission if he did not granted
     * for the app
     */
    private fun requestPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch("android.permission.CALL_PHONE")
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.parent_home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_child_menu_item -> {
                navigateToCreateAccountFragment()
                true
            }
            //If the user click in the profile icon in the app bar
            R.id.parent_profile_menu_item -> {
                //Set the user type to be display his profile as paren
                isUserProfileParent = true
                ////Set the parent data to be displayed in the profile
                viewModel.displayUserProfile(parent)
                true
            }
            R.id.log_out_menu_item -> {
                logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This function will navigate to the [UserProfileFragment]
     * @param isParent:the user type to display (parent or child)
     * @param user: the user data to be displayed
     */
    private fun navigateToUserProfile(isParent: Boolean, user: User) {
        val action = ParentHomeFragmentDirections.actionParentHomeFragmentToUserProfileFragment(
            userProfile = user,
            isParentProfile = isParent
        )
        findNavController().navigate(action)
    }


    /**
     * This function will navigate to [CreateAccountFragment] passing the user type as child to be created
     * in the destination and his parent data since in our app every child need to have parent to exist
     */
    private fun navigateToCreateAccountFragment() {
        val action = ParentHomeFragmentDirections
            .actionParentHomeFragmentToCreateAccountFragment(false, parent)
        findNavController().navigate(action)
    }

    /**
     * This function will be called when the user click on the
     * log out option menu item .
     * It will log out the parent user
     */
    private fun logOut() {
        navigateToLogInFragment()
    }

    /**
     * This function will navigate to [LoginFragment]
     */
    private fun navigateToLogInFragment() {
        val action = ParentHomeFragmentDirections.actionParentHomeFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    /**
     * This function will read every hing in the map
     */
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        var location = currentChild?.location?.last()
        Log.i(TAG, "map:$location")
        if (location == null) {
            Snackbar.make(binding.root, getString(R.string.no_location), Snackbar.LENGTH_SHORT)
                .show()
        } else {
            displayChildLocation(
                LatLng(
                    location.latLocation.toDouble(),
                    location.longLocation.toDouble()
                ), p0
            )
        }
    }

    private fun displayChildLocation(mCurrentLocation: LatLng, mMap: GoogleMap) {

        mMap.clear()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, DEFAULT_ZOOM.toFloat()))
        mMap.addMarker(MarkerOptions().position(mCurrentLocation))


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
        const val TAG = "ParentHomeFragment"
        const val PARENT = "parent"
        private const val DEFAULT_ZOOM = 15
    }
}