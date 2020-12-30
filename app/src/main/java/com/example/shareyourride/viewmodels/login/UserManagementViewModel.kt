/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.viewmodels.login

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.bvillarroya_creations.shareyourride.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


/**
 * View model that handles the user account
 */
class UserManagementViewModel(application: Application) : AndroidViewModel(application) {

    //region constants
    companion object
    {
        /**
         * Result code used in the login operation
         */
        const val LOGIN_OPERATION_RESULT_CODE = 123
    }
    //endregion

    //region private variables
    /**
     * Firebase authentication API
     */
    private val mAuthApi = AuthUI.getInstance()

    /**
     * Current User data
     */
    private val userData: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser


    private val context = getApplication<Application>().applicationContext
    //endregion

    //region user management public functions

    /**
     * Creates the intent that shows the login window
     * This window will allow to log with mail, google and facebook
     *
     * @return: The intent to open the activity for login
     */
    fun getSignInIntent(): Intent? {

        try {

            //Select authentication providers
            val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.FacebookBuilder().build()
            )

            //launch the authentication activity
            return mAuthApi.createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.full_title) // Set logo drawable
                .setTheme(R.style.LoginTheme) // Set theme
                //.setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .build()
        }
        catch (ex: java.lang.Exception)
        {
            Log.e("UserManagementViewModel", "SYR -> Unable to create the login intent")
            ex.printStackTrace()
        }

        return null
    }

    /**
     * Process the login result. If the result is ok, go to the initial window
     * else, print a toast
     *
     * @param resultCode: If the operation has performed successfully
     * @param data: The data associated to the login operation inside
     * an intent
     *
     * @return: true if the operation success, false otherwise
     */
    fun processLoginResult(resultCode: Int, data: Intent?): Boolean
    {
        try
        {
            Log.d("UserManagementViewModel", "SYR -> Processing login result")
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Log.d("UserManagementViewModel", "SYR -> User has signed in ${userData?.displayName} - ${userData?.uid}")
                return true
            }
            else {
                Log.d("UserManagementViewModel", "SYR -> Sign process has failed " + "${response?.error?.errorCode}")
            }
        }
        catch (ex: Exception)
        {
            Log.e("UserManagementViewModel", "SYR -> Unable to process the login operation ${ex.message}")
            ex.printStackTrace()
        }

        return false
    }

    /**
     * CLoses the current session of the user
     */
    fun signOut()
    {
        try
        {
            mAuthApi.signOut(context).addOnCompleteListener {
                if (it.isSuccessful)
                {
                    Log.i(
                            "UserManagementViewModel", "SYR -> User " + "   ${userData?.displayName}  - " + "${userData?.uid} signs out")
                }
                else {
                    Log.e("UserManagementViewModel", "SYR -> unable to close session of User " + "   ${userData?.displayName}  - " + "${userData?.uid}")
                    Toast.makeText(context, context.getString(R.string.close_session_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("UserManagementViewModel", "SYR -> Unable to sign out user ${userData?.displayName} error: ${ex.message}}")
            ex.printStackTrace()
        }
    }

    /**
     * Deletes the user account
     */
    fun delete()
    {
        try {

            mAuthApi.delete(context).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        Log.i(
                                "UserManagementViewModel", "SYR -> The account ${userData?.displayName} - ${userData?.uid} has been deleted")
                    }
                    else
                    {
                        Log.e("UserManagementViewModel", "SYR -> Unable to delete account " + "   ${userData?.displayName}  - " + "${userData?.uid}")
                        Toast.makeText(context, context.getString(R.string.delete_account_error), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        catch (ex: Exception)
        {
            Log.e("UserManagementViewModel", "SYR -> Unable to delete account of user ${userData?.displayName} error: ${ex.message}}")
            ex.printStackTrace()
        }
    }

    //endregion

}