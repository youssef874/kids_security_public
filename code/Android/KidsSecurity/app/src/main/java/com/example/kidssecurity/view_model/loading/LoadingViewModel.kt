package com.example.kidssecurity.view_model.loading

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class LoadingViewModel(private val retrofitRepository: RetrofitDao) : ViewModel() {

    //This Property to describe weather all the parent related images has been downloaded or not
    private var _isParentUserImageAdded = MutableLiveData<Boolean?>()
    val isParentUserImageAdded: LiveData<Boolean?>
        get() = _isParentUserImageAdded

    //This Property to describe weather all the child related images has been downloaded or not
    private var _isChildUserRelatedImagesAdded = MutableLiveData<Boolean?>()
    val isChildUserRelatedImagesAdded: LiveData<Boolean?>
        get() = _isChildUserRelatedImagesAdded

    //This property will hold the parent child user data
    private var _childParent = MutableLiveData<Response<Parent>>()
    val childParent: LiveData<Response<Parent>>
        get() = _childParent


    fun getChildParent(child: Child) {
        viewModelScope.launch {
            _childParent.value = Response.Loading()
            try {
                _childParent.value = Response.Success(
                    retrofitRepository.getParentService().getParentChild(child.idChild)
                )
            } catch (e: Exception) {
                Log.e(TAG, "retrofit error: ${e.message}")
                _childParent.value = Response.Error(Parent(), e.message)
            }
        }
    }

    /**
     * This function will call writeFileIntoExternalDisc() to download all images of the parent and his children
     * and update _isParentUserImageAdded accordingly
     * @param parent:the parent user
     * @param context Tis param going to give the file path root the android system going to give to our app
     */
    fun downloadAllImageForParentUser(parent: Parent, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var childDownloadState = false
            var parentDownloadState = false
            try {
                if (context.getExternalFilesDir(null) != null) {
                    parent.children.forEach { child: Child ->
                        val file = createFileForChild(context, child)
                        if (file.exists()) {
                            file.delete()
                        }
                        val body = retrofitRepository.getImageService().getChildImage(child.idChild)
                        childDownloadState = writeFileIntoExternalDisc(file, body)
                    }

                    val file = createFileForParent(context, parent)
                    if (file.exists()) {
                        file.delete()
                    }
                    val body = retrofitRepository.getImageService().getParentImage(parent.idParent)
                    parentDownloadState = writeFileIntoExternalDisc(file, body)

                }
                _isParentUserImageAdded.postValue(childDownloadState && parentDownloadState)
            } catch (e: Exception) {
                Log.i(TAG, "${e.message}")
            }

        }
    }

    /**
     * This function will create the parent directory then it will
     * create the parent image file inside it
     */
    private fun createFileForParent(
        context: Context,
        parent: Parent
    ): File {
        val fileDir = File(
            context.getExternalFilesDir(null).toString() + "/parent/"
        )
        if (!fileDir.exists())
            fileDir.mkdir()
        return File(
            context.getExternalFilesDir(null).toString() + "/parent/" +
                    "${parent.loginParent}_${parent.id}.png"
        )
    }

    /**
     * This function will call writeFileIntoExternalDisc() to download all images of the child and his parent
     * and update _isChildUserRelatedImagesAdded accordingly
     * @param child:the child user
     * @param context Tis param going to give the file path root the android system
     * going to give to our app
     * @param parent: the user parent
     */
    fun downloadAllImagesForChildUser(child: Child, context: Context, parent: Parent) {
        viewModelScope.launch(Dispatchers.IO) {
            var childDownloadState = false
            var parentDownloadState = false
            try {
                val childFile = createFileForChild(context, child)
                if (childFile.exists()) {
                    childFile.delete()
                }
                val body = retrofitRepository.getImageService().getChildImage(child.idChild)
                childDownloadState = writeFileIntoExternalDisc(childFile, body)
                val parentFile = createFileForParent(context,parent)
                if (parentFile.exists())
                    parentFile.delete()
                val parentBody = retrofitRepository.getImageService().getParentImage(parent.idParent)
                parentDownloadState = writeFileIntoExternalDisc(parentFile,parentBody)
                _isChildUserRelatedImagesAdded.postValue(parentDownloadState && childDownloadState)
            } catch (e: Exception) {
                Log.e(TAG, "error: ${e.message}")
            }
        }
    }

    /**
     * This function will create the child directory then it will
     * create the child image file inside it
     */
    private fun createFileForChild(
        context: Context,
        child: Child
    ): File {
        val fileDir = File(
            context.getExternalFilesDir(null).toString() + "/children/"
        )
        if (!fileDir.exists())
            fileDir.mkdir()
        return File(
            context.getExternalFilesDir(null).toString() + "/children/" +
                    "${child.loginChild}_${child.idChild}.png"
        )
    }


    /**
     * This function will write [ResponseBody] data into a file in the external disc
     * @param file: the will be written in
     * @param body: the data that going to write
     * @return if the data is successfully written return true otherwise return false
     */
    private suspend fun writeFileIntoExternalDisc(file: File, body: ResponseBody): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            var bytes = ByteArray(4096)
            val fullSize = body.contentLength()
            var size = 0L
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            while (true) {
                val read = inputStream.read(bytes)
                if (read == -1)
                    break
                outputStream.write(bytes, 0, read)
                size += read
                Log.i(TAG, "file down: $size of $fullSize")
            }
            outputStream.flush()
            return true
        } catch (e: Exception) {
            Log.i(TAG, "${e.message}")
            return false
        } finally {
            inputStream?.close()

            outputStream?.close()
        }
    }

    companion object {
        const val TAG = "LoadingViewModel"
    }
}