package com.example.kidssecurity.view_model.create_account

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateViewModel(private val retrofitRepository: RetrofitDao) : ViewModel() {


     //This property will hold the new created user([Parent])

    private var _newParent = MutableLiveData<Response<Parent?>>()
    val newParent: LiveData<Response<Parent?>>
        get() = _newParent

    /**
     * This function will launch an asynchronous task to
     * make [MultipartBody] from the image uri pass it with
     * [Parent] data into the addParent() of the [ParentApiService]
     * who gonna send via http request to inset them into the database
     * @param requireActivity: activity needed to create the [MultipartBody]
     */
    fun createParent(
        name: String,
        phoneNumber: String,
        login: String,
        password: String,
        imageUri: Uri,
        requireActivity: Activity
    ) {
        viewModelScope.launch {
            val parent = Parent(
                nameParent = name,
                telParent = phoneNumber.toInt(),
                loginParent = login,
                pswParent = password
            )
            //Still fetching the data
            _newParent.value = Response.Loading()
            val stream = requireActivity.contentResolver.openInputStream(imageUri) ?: return@launch
            val request = RequestBody.create(
                MediaType.parse("image/png"),
                stream.readBytes()
            )// read all bytes using kotlin extension
            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image",
                    "${parent.loginParent}.jpg",
                    request
                )
                val addedParent: Parent =
                    retrofitRepository.getParentService().addParent(parent, filePart)
                //Data fetched successfully
                _newParent.value = Response.Success(addedParent)
            } catch (e: Exception) {
                //Something went wrong
                _newParent.value = Response.Error(Parent(),e.message)
            }
        }
    }

    /**
     * This function will launch an asynchronous task to
     * make [MultipartBody] from the image uri pass it with
     * [Parent] data into the addChild() of the [ChildApiService]
     * who gonna send via http request to inset them into the database
     * @param requireActivity: activity needed to create the [MultipartBody]
     */
    fun createChild(
        parentOfNewChild: Parent,
        name: String,
        phoneNumber: String,
        login: String,
        password: String,
        imagePath: Uri,
        requireActivity: Activity
    ) {
        viewModelScope.launch {
            val child = Child(
                nameChild = name,
                telChild = phoneNumber.toInt(),
                loginChild = login,
                pswChild = password
            )
            //Still fetching the data
            _newParent.value = Response.Loading()
            val stream = requireActivity.contentResolver.openInputStream(imagePath) ?: return@launch
            val request = RequestBody.create(
                MediaType.parse("image/jpg"),
                stream.readBytes()
            )

            try {
                val filePart = MultipartBody.Part.createFormData(
                    "image",
                    "${child.loginChild}.jpg",
                    request
                )
                retrofitRepository.getChildService().addChild(child, filePart,parentOfNewChild.idParent)
                val parent = retrofitRepository.getParentService().getParent(parentOfNewChild.idParent)
                Log.i(TAG,"$parent")
                //Data fetched successfully
                _newParent.value = Response.Success(parent)
            }catch (e: Exception){
                //Something went wrong
                _newParent.value = Response.Error(Parent(),e.message)
        }
    }

    }
    companion object {
        const val TAG = "CreateViewModel"
    }
}