package com.example.kidssecurity

import android.content.Context
import android.net.Uri
import com.example.kidssecurity.model.retrofit.entity.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

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

