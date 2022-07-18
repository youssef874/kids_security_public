package com.example.kidssecurity

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent
import com.google.android.material.textfield.TextInputLayout
import java.io.File


/**
 * This is an extension method of [TextInputLayout] to enable the error and display the message
 * @param isWong: if there error or not
 * @param msg: the msg is going to display if there an error
 */
fun TextInputLayout.setError(isWong: Boolean, msg:String = ""){
    if (isWong){
        isErrorEnabled =true
        error = msg
    }else{
        isErrorEnabled = false
        error = null
    }
}

/**
 * This extension function will hide the keyboard
 * @param view: the how from it is click we going to hide the keyboard
 */
fun Context.hideKeyboard(view: View){
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * This extension function will call hideKeyboard() method above of th [Context]
 * and this going to be accessible in any [Activity] in this project
 */
fun Activity.hideKeyBoard(){
    hideKeyboard(currentFocus ?: View(this))
}

/**
 * This extension function will call hideKeyboard() method above of th [Context]
 * and this going to be accessible in any [Fragment] in this project
 */
fun Fragment.hideKeyboard(){
    view?.let { activity?.hideKeyboard(it) }
}


private const val CHANNEL_ID = "chat"
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(message: String,name: String,context: Context){

    val intent = Intent(context,MainActivity::class.java)

    val pendingIntent: PendingIntent = PendingIntent.getActivities(
        context,
        NOTIFICATION_ID,
        arrayOf(intent),
        PendingIntent.FLAG_IMMUTABLE
    )

    var builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_message)
        .setContentTitle(context.getString(R.string.chat_notification_title,name))
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        createNotificationChannel(channel)
    }

    notify(NOTIFICATION_ID,builder.build())
}

fun NotificationManager.cancelNotification(){
    cancelAll()
}

suspend fun String.getUserUriOfPath(user: User,isParent: Boolean): Uri?{
    val file = File(this)
    return file.getUserImageUriOfFile(user, isParent)
}

suspend fun File.getUserImageUriOfFile(user: User, isParent: Boolean): Uri?{
    var uri: Uri? = null
    var fileIndex = 0

    if (listFiles() != null && listFiles().isNotEmpty() ){
        while (fileIndex < listFiles()!!.size && uri == null){
            val image = listFiles()!![fileIndex]
            if (isParent){
                val parent = user as Parent
                if (image.name.contains(parent.loginParent) &&
                    image.name.contains(parent.idParent.toString())){
                    uri = Uri.fromFile(image)
                }
            }else{
                val child = user as Child
                if (image.name.contains(child.loginChild)
                    && image.name.contains(child.idChild.toString())
                ) {
                    uri = Uri.fromFile(image)
                }
            }
            fileIndex ++
        }
    }
    return uri
}

