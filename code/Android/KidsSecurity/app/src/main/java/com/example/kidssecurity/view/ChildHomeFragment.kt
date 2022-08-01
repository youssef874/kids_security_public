package com.example.kidssecurity.view

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kidssecurity.R
import com.example.kidssecurity.checkBackgroundLocationAccessPermission
import com.example.kidssecurity.checkForegroundLocationAccessPermission
import com.example.kidssecurity.databinding.FragmentChildHomeBinding
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.view_model.child_home.ChildHomeViewModel
import com.example.kidssecurity.view_model.child_home.ChildHomeViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


class ChildHomeFragment : Fragment(), OnMapReadyCallback {

    // Binding object instance corresponding to the fragment_child_home.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentChildHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val retrofitRepository = Repository.retrofitRepository

    private val factory = ChildHomeViewModelFactory(retrofitRepository)
    private val viewModel: ChildHomeViewModel by viewModels { factory }

    //This variable will hold the user data as [Child]
    private lateinit var child: Child
    //This variable will hold the user parent data
    private lateinit var parentChild: Parent

    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
    // how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient
// has a new Location
    private lateinit var locationCallback: LocationCallback

    //This property will hold the call permission result
    private lateinit var requestCallPermissionLauncher: ActivityResultLauncher<String>
    //This property will hold the foreground Access Location permission result
    private lateinit var requestAccessLocationPermissionLauncher: ActivityResultLauncher<String>
    //This property will hold the background Access Location permission result
    private lateinit var requestBackgroundAccessLocationLauncher: ActivityResultLauncher<String>
    //This property will hold SMS permission result
    private lateinit var requestSmsPermissionLauncher: ActivityResultLauncher<String>

    // Current child Location
    private var currentLocation: LatLng? = null
    //If the location is accessible from the foreground
    private var isLocationAccessible: Boolean? = null
    //If the location is accessible from the background
    private var isTrackableInBackground: Boolean? = null
    //This will hold the message of the SMS
    private var message: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            child = it.getParcelable<Child>(CHILD) as Child
            parentChild = it.getParcelable<Parent>(CHILD_PARENT) as Parent
        }
        //Enable option menu
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()

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

    private fun startLocationUpdates() {
        createLocationRequest()

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
               p0.lastLocation?.let {
                   currentLocation = LatLng(it.latitude,it.longitude)
                   viewModel.updateCurrentLocation(LatLng(it.latitude,it.longitude))
               }
            }
        }
    }

    private fun createLocationRequest() {
        isLocationAccessible =
            context?.checkForegroundLocationAccessPermission(requestAccessLocationPermissionLauncher)
        locationRequest = LocationRequest.create().apply {
            // Sets the desired interval for
            // active location updates.
            // This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(60)

            // Sets the fastest rate for active location updates.
            // This interval is exact, and your application will never
            // receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location
            // updates are delivered. Updates may be
            // delivered sooner than this interval
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChildHomeBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.chat_map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        startLocationUpdates()
        checkSystemSettingForLocalisation()

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
            Looper.getMainLooper())

        requestCallPermissionLauncher = registerForActivityResult(
            ActivityResultContracts
            .RequestPermission()){isGranted ->
            //If the call permission is granted
            if (!isGranted){
                displayWarningDialog()
            }else{
                makeCall(parentChild)
            }
        }

        requestAccessLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()){
            isLocationAccessible = it
        }

        requestBackgroundAccessLocationLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()
        ) {
            isTrackableInBackground = it
        }

        requestSmsPermissionLauncher = registerForActivityResult(
            ActivityResultContracts
                .RequestPermission()){isGranted ->
            if (isGranted){
                message = getString(R.string.urgent_message
                    ,"https://maps.google.com/?q=${currentLocation!!.latitude}," +
                            "${currentLocation!!.longitude}")
                sendSMS(parentChild.telParent.toString(),message!!)
            }
        }

        binding.childCallFb.setOnClickListener {
            call()
        }

        binding.childUrgentFb.setOnClickListener {
            message = getString(R.string.urgent_message
                ,"https://maps.google.com/?q=${currentLocation!!.latitude}," +
                        "${currentLocation!!.longitude}")
            requestSensSmsRequest(message!!)
        }

        binding.childHomeLocalisation.setOnClickListener {
            isTrackableInBackground =
                context?.checkBackgroundLocationAccessPermission(requestBackgroundAccessLocationLauncher)
            isLocationAccessible =
                context?.checkForegroundLocationAccessPermission(requestAccessLocationPermissionLauncher)
            if (isLocationAccessible == true || isTrackableInBackground == true) {
                setCurrentLocation()
            } else {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.Localisation_Error),
                        Snackbar.LENGTH_SHORT
                    )
                    .show()
            }
        }

        binding.childChatFb.setOnClickListener {
            navigateToChatFragment()
        }

        viewModel.currentLocation.observe(viewLifecycleOwner){
            if (it != null){
                viewModel.postCurrentLocation(it.latitude.toLong(),it.longitude.toLong(),child.idChild)
            }
        }

    }

    /**
     * This function to check if the access location is not on
     */
    private fun checkSystemSettingForLocalisation() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    displayWarningDialogForLocalisation()
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    /**
     * This will display a warning message if the location not turn on
     */
    private fun displayWarningDialogForLocalisation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.setting_warning_title)
            .setMessage(getString(R.string.setting_warning_body)
            )
            .setPositiveButton(R.string.call_dialog_positive_button){_,_ ->
                openLocalisationSetting()
            }
            .show()
    }

    /**
     * Open the localization setting so the user can turn on the localization
     */
    private fun openLocalisationSetting() {
        val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1)
        Toast.makeText(
            requireContext(),
            getString(R.string.enable_location_access),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        if (isLocationAccessible == true)
            startLocationUpdates()
    }

    private fun setCurrentLocation() {
       mMap?.let {
           if (currentLocation != null){
               it.clear()
               it.addMarker(MarkerOptions().position(currentLocation!!))
               it.moveCamera(CameraUpdateFactory
                   .newLatLngZoom(currentLocation!!, DEFAULT_ZOOM.toFloat()))
           }
       }
    }

    private fun requestSensSmsRequest(message: String) {
        context?.let {
            when{
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED ->{
                    requestAccessLocationPermissionLauncher.launch("android.permission.SEND_SMS")
                }else ->{
                if(currentLocation != null){
                    sendSMS(parentChild.telParent.toString(),message)
                    Toast.makeText(requireContext(),getText(R.string.message_sent),Toast.LENGTH_SHORT).show()
                }
                }
            }
        }
    }

    private fun navigateToChatFragment() {
        val action = ChildHomeFragmentDirections
            .actionChildHomeFragmentToChatFragment(
                sender = child,
                receiver = parentChild,
                isSenderParent = false
            )
        findNavController().navigate(action)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        SmsManager.getDefault().sendTextMessage(phoneNumber, null
            , message, null, null)
    }

    /**
     * This function will be called when the call floating button pressed
     */
    private fun call() {
        requestPermission()
        displayWarningDialog()
    }

    /**
     * This function will ask the user for phone call permission if he did not granted
     * for the app
     */
    private fun requestPermission() {
        context?.let {
            when{
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED ->{
                    requestCallPermissionLauncher.launch("android.permission.CALL_PHONE")
                }
            }
        }
    }

    /**
     * This function will be responsible in making the phone call to
     * the parent
     * @param parentChild:the child parent who is going to make phone call to
     */
    private fun makeCall(parentChild: Parent) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:${parentChild.telParent}")
        startActivity(intent)
    }

    /**
     * This function will display a waring dialog to the child [User]
     * to inform him that he is going to call his child who is displayed in
     * the message with his phone number and ask for conformation for make the phone call
     */
    private fun displayWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.call_warning_dialog_title)
            .setMessage(getString(R.string.call_warning_dialog_body,parentChild.telParent
                ,parentChild.nameParent)
            )
            .setPositiveButton(R.string.call_dialog_positive_button){_,_ ->
                makeCall(parentChild)
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.child_home_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.child_home_log_out_menu_item ->{
                logOut()
                true
            }
            R.id.child_home_profile_menu ->{
                navigateToUserProfile(child)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    /**
     * This function will navigate to the [UserProfileFragment]
     * @param isParent:the user type to display (parent or child)
     * @param user: the user data to be displayed
     */
    private fun navigateToUserProfile(child: Child) {
        val action = ChildHomeFragmentDirections
            .actionChildHomeFragmentToUserProfileFragment(child,false)
        findNavController().navigate(action)
    }

    /**
     * This function will be called when the user click on the
     * log out option menu item .
     * It will log out the user [Child]
     */
    private fun logOut() {
        navigateToLoginFragment()
    }

    /**
     * This function will navigate to [LoginFragment]
     */
    private fun navigateToLoginFragment() {
        val action = ChildHomeFragmentDirections.actionChildHomeFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        isTrackableInBackground =
            context?.checkBackgroundLocationAccessPermission(requestBackgroundAccessLocationLauncher)
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object{
        const val CHILD = "child"
        const val TAG = "ChildHomeFragment"
        const val CHILD_PARENT = "child_parent"
        private const val DEFAULT_ZOOM = 15
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
    }
}