package com.example.kidssecurity.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.FragmentCreateAccountBinding
import com.example.kidssecurity.hideKeyboard
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.view_model.create_account.CreateViewModel
import com.example.kidssecurity.view_model.create_account.CreateViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent

/**
 * Tis class is a ui controller to The create account view
 */
class CreateAccountFragment : Fragment() {

    // Binding object instance corresponding to the fragment_create_account.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentCreateAccountBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val retrofitRepository = Repository.retrofitRepository

    private val factory = CreateViewModelFactory(retrofitRepository)

    private val viewModel: CreateViewModel by viewModels { factory }

    //This variable will hold intent result of open the camera (the taken picture)
    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>

    //This variable will hold permission result access into internal storage(read and write)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    //This variable will hold intent result of open the gallery (the chosen picture)
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    //The user type(true if it is parent child for false)
    private var isParent: Boolean? = null

    private var parentOfNewChild: Parent? = null

    //The image uri whether from camera shot or gallery image
    private lateinit var imagePath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get user type argument passed from login fragment
        arguments?.let {
            isParent = it.get(LoginFragment.IS_PARENT) as Boolean
            if (!(isParent as Boolean)){
                parentOfNewChild = it.getParcelable(ParentHomeFragment.PARENT)!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateAccountBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Listen to the permission request whether granted or not
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    //If the permission granted by the user call addPictureFromGallery()
                    addPictureFromGallery()
                }
            }

        //Listen to the open camera  intent result
        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //If the picture taken successfully
                    handlePicture(result.data)
                }
            }

        //Listen to the open the gallery  intent result
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //If the picture chosen successfully from the gallery
                    handlePicture(result.data)
                }
            }

        //When the take picture button clicked
        binding.takePictureButton.setOnClickListener {
            takePicture()
        }

        //When the add picture button clicked
        binding.addPictureButton.setOnClickListener {
            askPermission()
            addPictureFromGallery()
        }

        //When the finish button clicked
        binding.finishButton.setOnClickListener {
            binding.loadingCreate.visibility = View.VISIBLE
            binding.finishButton.visibility = View.INVISIBLE
            createUser(isParent)
        }

        //Listen to newParent changes through this fragment life cycle with the architecture component help
        viewModel.newParent.observe(viewLifecycleOwner) {
            if (it != null) {
                //If the created user is parent
                if (it.data != null && it.data != Parent()) {
                    //If the parent added successfully to the database
                    navigateToLoadingFragment(true, it.data)
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.create_account_error),
                Toast.LENGTH_LONG).show()
            }
        }

        binding.createLayoutContainer.setOnClickListener {
            hideKeyboard()
        }

    }

    /**
     * This function responsible for navigation to [LoadingFragment]
     * @param isUserParent: If true the user is parent if not then false
     * @param user: The user data
     */
    private fun navigateToLoadingFragment(isUserParent: Boolean, user: User) {

        val action = CreateAccountFragmentDirections.actionCreateAccountFragmentToLoadingFragment(
            isUserParent,
            user
        )
        findNavController().navigate(action)
    }

    /**
     * This function responsible getting the user input
     * pass them as parameters to [CreateViewModel] createParent()
     * if the user is parent createChild() if not,who gonna add them
     * to the database
     * @param isParent: If true the user is parent if not then false
     */
    private fun createUser(isParent: Boolean?) {
        val name = binding.fullNameEditText.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val login = binding.createLoginEditText.text.toString()
        val password = binding.createPasswordEditText.text.toString()
        if (name.isNotEmpty() && phoneNumber.isNotEmpty() && login.isNotEmpty() && password.isNotEmpty()) {
            isParent?.let {
                if (it) {
                    viewModel.createParent(
                        name,
                        phoneNumber,
                        login,
                        password,
                        imagePath,
                        requireActivity()
                    )
                } else {
                    parentOfNewChild?.let { it1 ->
                        viewModel.createChild(
                            it1,
                            name,
                            phoneNumber,
                            login,
                            password,
                            imagePath,
                            requireActivity()
                        )
                    }
                }
            }
        }
    }

    /**
     * This function will ask user permission to access into External storage
     */
    private fun askPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    permissions[0]
                ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            it,
                            permissions[1]
                        ) != PackageManager.PERMISSION_GRANTED -> {
                    requestPermissionLauncher.launch(permissions[0])
                    requestPermissionLauncher.launch(permissions[1])
                }

            }
        }
    }

    /**
     * This function wil open the gallery and allow user to pick image
     * to be added as their profile picture
     */
    private fun addPictureFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryResultLauncher.launch(intent)
    }

    /**
     * This function handle image when picked from gallery or took through camera
     * and set it is uri to imagePath variable declared above to passed in
     * [CreateViewModel] create methods
     * @param data : the actions that allow to get the photo via camera or gallery
     */
    private fun handlePicture(data: Intent?) {
        data?.data?.let {
            imagePath = it
        }
    }

    /**
     * This function will open the camera
     */
    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(cameraIntent)
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
        const val TAG = "CreateAccountFragment"
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
    }

}