package com.example.kidssecurity.view_model.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.launch

/**
 * This class will be link the data layer and login part of layer to maintain MVVM architecture
 * @param retrofitRepository: give access to all network service of this app
 */
class LoginViewModel(private val retrofitRepository: RetrofitDao) : ViewModel() {

    //This is variable to identify if the user is parent or not(child)
    private var _isUserParent = MutableLiveData<Boolean>()

    //This is a getter for _isUserParent
    val isUserParent: LiveData<Boolean>
        get() = _isUserParent

    //This Property will hold The the user information as father if he is authenticated
    //an identified as a father
    private var _parent = MutableLiveData<Response<Parent>?>()
    val parent: LiveData<Response<Parent>?>
        get() = _parent

    //This Property will hold The the user information as child if he is authenticated
    //an identified as a child
    private var _child = MutableLiveData<Response<Child>?>()
    val child: LiveData<Response<Child>?>
    get() = _child

    /**
     * This function will start an asynchronous task of checking
     * if this child is authenticated or not by calling getAuthenticatedChild()
     * of ChildApiService who will send a request to the server for verification
     * @param name: the user login
     * @param psw: the user password
     */
     fun getAuthenticatedChild(name:String,psw: String) {
         viewModelScope.launch {
             //Still fetching the data
             _child.value = Response.Loading()
             try {
                 //Data fetched successfully
                 _child.value =
                     Response.Success(retrofitRepository.getChildService().getAuthenticatedChild(
                         name = name,
                         psw = psw                     ))
             }catch (e: Exception){
                 //Something went wrong
                 _child.value = Response.Error(Child(),e.message)
             }
         }
    }

    /**
     * This function will start an asynchronous task of checking
     * if this parent is authenticated or not by calling getAuthenticatedParent()
     * of ParentApiService who will send a request to the server for verification
     * @param name: the user login
     * @param psw: the user password
     */
    fun getAuthenticatedParent(name:String,psw: String){
        viewModelScope.launch {
            //Still fetching the data
            _parent.value = Response.Loading()
            try {
                //Data fetched successfully
                _parent.value =
                    Response.Success(retrofitRepository.getParentService().getAuthenticatedParent(
                        name = name,
                        psw = psw
                    ))
            }catch (e: Exception){
                //Something went wrong
                _parent.value = Response.Error(Parent(),e.message)
                Log.i(TAG, "${e.message}")
            }
        }
    }

    /**
     * Change _isUserParent values to help keep track of
     * the user type (parent or not)
     * @param mIsUserParent: true if user is parent false if not
     */
    fun changeUser(mIsUserParent: Boolean) {
        _isUserParent.value = mIsUserParent
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}