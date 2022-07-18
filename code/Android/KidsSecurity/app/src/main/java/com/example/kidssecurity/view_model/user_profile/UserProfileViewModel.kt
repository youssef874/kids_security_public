package com.example.kidssecurity.view_model.user_profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidssecurity.getUri
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class UserProfileViewModel : ViewModel() {

    //This property will hold user who is going to be displayed in the associated fragment
    private var _user = MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    //This property will hold the user image uri
    private var _userImageUri = MutableLiveData<Uri?>()
    val userImageUri: LiveData<Uri?>
        get() = _userImageUri

    /**
     * This function will set the user data in the _user variable
     * @param user:the user who is going in be set in the property
     */
    fun setUser(user: User) {
        _user.value = user
    }

    /**
     * This function will call getUri() function in the another thread to get
     * the user image uri and set it in _userImageUri
     * @param user: the user who the image will be searching for
     * @param isParent: the user typ(parent or child)
     * @param context: this will get us the path roo the android system going
     * to give to our app
     */
    fun setUserUri(user: User, isParent: Boolean, context: Context) {
        viewModelScope.launch{
            _userImageUri.postValue(getUri(user,isParent,context,Dispatchers.IO))
        }
    }

}