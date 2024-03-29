/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.permissions

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
    /**
     * The list of permission that the Manager has to request to the user
     */
    val mPermissionList = mutableListOf<String>()

    /**
     * The result code of the permission request. Each functionality uses an unique One
     */
    val mRequestPermissionsCode: Int = 23489

    /**
     * List of permissions that aren't granted by the user o sensors aren't available
     */
    val mPermissionsRejected = mutableListOf<String>()
    //endregion

    //region functionalities list
    /**
     * Add a new functionality to the list of permission to request
     * @param functionality: name of the functionality, defined in Manifest.permission.
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
                    " ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Delete a functionality from the list of functionality to request
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
                    " ${ex.message}")
            ex.printStackTrace()
        }

    }

    /**
     * Add a new functionality to the list of permission to request
     * @param functionality: name of the functionality, defined in Manifest.permission.
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
                    " ${ex.message}")
ex.printStackTrace()
        }
    }

    /**
     * Delete a functionality from the list of functionalities to request
     *
     * @param functionality: The name of the functionality to remove
     */
    private fun removeRejectedFunctionality(functionality: String)
    {
        try
        {
            mPermissionsRejected.remove(functionality)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to remove functionality to the list of permissions to request ${ex.message}")
            ex.printStackTrace()
        }

    }
    //endregion

    /**
     * Check if the functionality has its permission granted. If not, ask for them
     */
    fun checkPermissions() {
        try {
            var askForPermission = false
            var permissionsDenied = mutableListOf<String>()
            mPermissionList.forEach{
                val permission = ContextCompat.checkSelfPermission(activity, it)

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    askForPermission = true
                    permissionsDenied.add(it)
                    Log.i("SYR", "SYR -> Permission to access to functionality $it denied")
                }
                else
                {
                    Log.i("SYR", "SYR -> Permission to access to functionality $it granted")
                }
            }

            if (askForPermission)
            {
                makeRequest(permissionsDenied)
            }
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to check or ask for permissions ${ex.message}")
            ex.printStackTrace()
        }
    }


    /**
     * Request to the user to grant permissions
     *
     * @param list: The list of permissions to ask
     */
    private fun makeRequest(list: MutableList<String>)
    {
        try
        {
            Log.i("SYR", "SYR -> Asking permissions for $list")
            activity.requestPermissions(list.toTypedArray(),mRequestPermissionsCode)
        }
        catch(ex: Exception)
        {
            Log.e("SYR", "SYR -> Unable to make the permission request ${ex.message}")
            ex.printStackTrace()
        }
    }

    /**
     * Manage the result of the permission request
     * @param requestCode: the identifier of the request permission transaction
     * @param permissions: List of the requested permissions
     * @param grantResults: the result of each permission request individually
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
                    if (grantResults.isEmpty() || it != PackageManager.PERMISSION_GRANTED)
                    {
                        processPermissionDenied(permissions[i])
                    }
                    else
                    {
                        processPermissionGranted(permissions[i])
                    }
                    i++
                }
            }
        }
    }

    /**
     * Process the rejection of the permission request
     *
     * @param permission: The name of the denied permission
     */
    private fun processPermissionDenied(permission: String)
    {
        Log.i(ContentValues.TAG, "Permission for $permission has been denied by user")
        addRejectedFunctionality(permission)
    }

    /**
     * Process the acceptation of the permission request
     *
     * @param permission: The name of the denied permission
     */
    private fun processPermissionGranted(permission: String)
    {
        removeRejectedFunctionality(permission)
        Log.i(ContentValues.TAG, "Permission has been granted by user")
    }
}