package com.example.kidssecurity.view_model.parent_home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.kidssecurity.getUri
import com.example.kidssecurity.model.ChildListItem
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Location
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.internal.synchronized

class ParentHomeViewModel(private val retrofitRepository: RetrofitDao) : ViewModel() {

    //This property will hold list of all parent children in [ChildListItem]
    private var _childrenListWithUri = MutableLiveData<List<ChildListItem>?>()
    val childrenListWithUri: LiveData<List<ChildListItem>?>
        get() = _childrenListWithUri

    //This property will hold the parent current selected child
    private var _currentChild = MutableLiveData<Child?>()
    val currentChild: LiveData<Child?>
        get() = _currentChild

    //This property hold the user data that will be displayed  in UserProfileFragment
    private var _navigateToUserProfile = MutableLiveData<User?>()
    val navigateToUserProfile: LiveData<User?>
        get() = _navigateToUserProfile

    private var _selectedChildCurrentLocation = MutableLiveData<Location?>()
    var selectedChildCurrentLocation: LiveData<Location?> = _selectedChildCurrentLocation

    fun getCurrentLocation(child: Child) {
        viewModelScope.launch {
            Log.i(TAG,"after launch: ${child.idChild}")
                val result = retrofitRepository.getLocationService().getChildLocation(child,this)
                Log.i(TAG,"before collect: ${child.idChild}")
                result.collect{
                    if (it is Response.Success && it.data?.size != 0 ){
                        Log.i(TAG,"after collect: ${child.idChild},${it.data?.last()?.idLocation}")
                        _selectedChildCurrentLocation.postValue(
                            it.data?.last()
                        )
                    }
                }
        }

    }

    /**
     * This function will set data to  _navigateToUserProfile property
     * @param user: the user data that will be displayed
     */
    fun displayUserProfile(user: User) {
        _navigateToUserProfile.value = user
    }

    /**
     * reset _navigateToUserProfile property
     */
    fun displayProfileComplete(firstChild: Child) {
        _navigateToUserProfile.value = null
        _currentChild.value = firstChild
    }

    fun displayChatComplete(firstChild: Child) {
        _currentChild.value = firstChild
    }

    /**
     * This function create children list
     * @param parent: the parent user
     * @param context: the context the will get you  the file path
     */
    fun mapAllChildrenImageToTheirUri(parent: Parent, context: Context) {
        val list = ArrayList<ChildListItem>()
        viewModelScope.launch {
            parent.children.forEach { child: Child ->
                val uri = getUri(child, false, context, Dispatchers.IO)
                list.add(
                    ChildListItem(uri, child)
                )
            }
            _childrenListWithUri.postValue(list)
        }
    }

    /**
     * This function set _currentChild properties
     * @param child: the parent current selected  children
     */
    fun postTheCurrentChild(child: Child) {
        child.isTrackable = true
        _currentChild.value = child
    }


    companion object {
        const val TAG = "ParentHomeViewModel"
    }
}

data class ChildLocation(
    var location: Location = Location(),
    var idChild: Long = -1
)

data class TrackableChild(
    var child: Child = Child(),
    var isTrackable: Boolean = true
)