package com.example.shareyourride.Permissions

import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/*
    Class in charge of check the permissions
 */
class PermissionsManager(val activity: Activity): ActivityCompat.OnRequestPermissionsResultCallback {

    //region properties
    /*
        The list of permission that the Manager has to request to the user
    */
    val mPermissionList = mutableListOf<String>()

    /*
        The result code of the permission request. Each functionality uses an unique one
    */
    val mRequestPermissionsCode: Int = 2348974

    /*
    List of permissions that aren't granted by the user o sensors aren't available
 */
    val mPermissionsRejected = mutableListOf<String>()
    //endregion

    //region functionalities list
    /*
        Add a new functionality to the list of permission to request
        @param functionality: name of the functionality, defined in Manifest.permission.
     */
    fun addFunctionality(functionality: String)
    {
        try
        {
            if (mPermissionList.indexOf(functionality) < 0 )
            {
                mPermissionList.add(functionality)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to add functionality to the list of permissions to request" +
                    " ${ex.message} - ${ex.stackTrace}")
        }
    }

    /*
        Delete a functionality from the list of functionalities to request
     */
    fun removeFunctionality(functionality: String)
    {
        try
        {
            mPermissionList.remove(functionality)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove functionality to the list of permissions to request" +
                    " ${ex.message} - ${ex.stackTrace}")
        }

    }

    /*
       Add a new functionality to the list of permission to request
       @param functionality: name of the functionality, defined in Manifest.permission.
    */
    private fun addRejectedFunctionality(functionality: String)
    {
        try
        {
            if (mPermissionsRejected.indexOf(functionality) < 0 )
            {
                mPermissionsRejected.add(functionality)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to add functionality to the list of permissions to request" +
                    " ${ex.message} - ${ex.stackTrace}")
        }
    }

    /*
        Delete a functionality from the list of functionalities to request
     */
    private fun removeRejectedFunctionality(functionality: String)
    {
        try
        {
            mPermissionsRejected.remove(functionality)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove functionality to the list of permissions to request" +
                    " ${ex.message} - ${ex.stackTrace}")
        }

    }
    //endregion

    /*
      Check if the functionality has its permission granted. If not, ask for them
   */
    fun checkPermissions() {
        try {
            var askForPermission = false
            mPermissionList.forEach{
                val permission = ContextCompat.checkSelfPermission(activity, it)

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    askForPermission = true
                    Log.i("SYR", "SYR -> Permission to $it denied")
                }
            }

            if (askForPermission)
            {
                makeRequest()
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to check or ask for permissions" +
                    " ${ex.message} - ${ex.stackTrace}")
        }
    }


    /*
        Request to the user to grant permissions
     */
    private fun makeRequest()
    {
        try
        {
            Log.e("SYR", "SYR -> Asking permissions for $mPermissionList")
            ActivityCompat.requestPermissions(activity, mPermissionList.toTypedArray(),mRequestPermissionsCode)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to make the permission request" +
                    " ${ex.message} - ${ex.stackTrace}")
        }
    }

    /*
        Manage the result of the permission request
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            mRequestPermissionsCode ->
            {
                var i = 0
                grantResults.forEach {
                    val permission = permissions[i]
                    if (grantResults.isEmpty() || it != PackageManager.PERMISSION_GRANTED)
                    {
                        Log.i(ContentValues.TAG, "Permission for $permission has been denied by user")
                        addRejectedFunctionality(permission)
                    }
                    else
                    {
                        removeRejectedFunctionality(permission)
                        Log.i(ContentValues.TAG, "Permission has been granted by user")
                    }
                    i++
                }
            }
        }
    }
}