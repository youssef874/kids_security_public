package com.example.kidssecurity

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

//Tis const hold the Chat notification channel id
private const val CHANNEL_ID = "chat"
//Tis const hold the Chat notification id
private const val NOTIFICATION_ID = 0

/**
 * This function will send a the notification to the user
 * @param message: The notification body
 * @param title: The notification title
 * @param context: The Android context to get the String recourses
 */
fun NotificationManager.sendNotification(message: String, title: String, context: Context){

    val intent = Intent(context,MainActivity::class.java)

    val pendingIntent: PendingIntent = PendingIntent.getActivities(
        context,
        NOTIFICATION_ID,
        arrayOf(intent),
        PendingIntent.FLAG_IMMUTABLE
    )

    var builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_message)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notificationChannel(context)

    notify(NOTIFICATION_ID,builder.build())
}

/**
 * This function will check if the device android version is Android 8.0 and above
 * the it call createNotificationChannel() of [NotificationManager]
 */
fun NotificationManager.notificationChannel(context: Context){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        createNotificationChannel(channel)
    }
}

/**
 * This function will cancel all the notifications
 */
fun NotificationManager.cancelNotification(){
    cancelAll()
}

/**
 * This suspend function will create a [File] from [String] path the call getUserImageUriOfFile()
 * the [File] extension function and run in the background thread
 * @param user: The user which his image that will search for
 * @param isParent: To identify if user is [Parent] or [Child]
 * @return the user image [Uri]
 */
suspend fun String.getUserUriOfPath(user: User,isParent: Boolean): Uri?{
    val file = File(this)
    return file.getUserImageUriOfFile(user, isParent)
}

/**
 * This suspend function will get the user image from File as extension function
 * and run in the background thread
 * @param user: The user which his image that will search for
 * @param isParent: To identify if user is [Parent] or [Child]
 * @return the user image [Uri]
 */
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

/**
 * This function will check if [Manifest.permission.ACCESS_COARSE_LOCATION] and
 * [Manifest.permission.ACCESS_FINE_LOCATION] are granted if not it will ask the user to grand
 * permission to the app.
 */
fun Context.checkForegroundLocationAccessPermission(launcher: ActivityResultLauncher<String>): Boolean {
    val permissions = arrayOf(
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.ACCESS_FINE_LOCATION"
    )

    return if (
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ){
        launcher.launch(permissions[0])
        launcher.launch(permissions[1])
        false
    }else{
        true
    }
}

/**
 * This function will call checkForegroundLocationAccessPermission() and check
 * [android.permission.ACCESS_BACKGROUND_LOCATION] are granted if not it will ask the user to grand
 * permission to the app.
 */
fun Context.checkBackgroundLocationAccessPermission(launcher: ActivityResultLauncher<String>): Boolean{
    val backgroundPermission = "android.permission.ACCESS_BACKGROUND_LOCATION"
    return if (
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ){
        launcher.launch(backgroundPermission)
        true
    }else{
        false
    }
}

/**
 * This will check for [android.permission.CALL_PHONE] are granted if not it will ask the user to grand
 * permission to the app.
 */
fun Context.checkCallPermission(launcher: ActivityResultLauncher<String>): Boolean{
    val permission = "android.permission.CALL_PHONE"
    return if (
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
    ){
        launcher.launch(permission)
        true
    }else{
        false
    }
}

