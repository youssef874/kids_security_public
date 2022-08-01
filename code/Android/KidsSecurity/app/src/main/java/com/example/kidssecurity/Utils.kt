package com.example.kidssecurity

import android.content.Context
import android.net.Uri
import com.example.kidssecurity.model.retrofit.entity.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * This function set up the file path to get the user image uri, then call getUserUriOfPath()
 * of [File] extension function in the desired [CoroutineDispatcher]
 * @param user: the user which his image will be looking for
 * @param isParent: To identify the user either [Parent] or [Child]
 * @param context: The Android [Context] to get the application root file given by the Android system
 * @return: the user image [Uri]
 */
suspend fun getUri(user: User, isParent: Boolean, context: Context,dispatcher: CoroutineDispatcher): Uri? {
    var uri: Uri? = null
    var path = context.getExternalFilesDir(null).toString()
    withContext(dispatcher){
        if (isParent) {
            path += "/parent/"
            uri = path.getUserUriOfPath(user, isParent)
        }else{
            path+= "/children/"
            uri = path.getUserUriOfPath(user, isParent)
        }
    }
    return uri
}

