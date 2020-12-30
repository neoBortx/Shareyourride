/*
 * Copyright (c) 2020. Borja Villarroya Rodriguez, All rights reserved
 */

package com.example.shareyourride.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.shareyourride.userplayground.MainActivity
import com.example.shareyourride.viewmodels.settings.SettingsViewModel
import com.example.shareyourride.viewmodels.login.UserManagementViewModel

/**
 * Class to manage the login window
 * THe log operation is performed by Firebase authentication as is explained in:
 * https://firebase.google.com/docs/auth/android/firebaseui
 *
 * The code  https://github.com/firebase/snippets-android
 */
class LoginActivity : AppCompatActivity() {

    //region view models
    /**
     * View model that handles the user information, hides the login provider
     * in this layer
     */
    private val userManagementViewModel: UserManagementViewModel by viewModels()

    //region view models
    /**
     *
     */
    private val settingsViewModel: SettingsViewModel by viewModels()
    //endregion

    /**
     * Shows the Login window
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInWindow()
    }

    //region login handlers
    /**
     * Handles the result of the login window
     *
     * @param requestCode: The identifier of the procedure
     * @param resultCode: The result of the login operation
     * @param data: Login data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {

            Log.d("LoginActivity", "SYR -> Processing onActivityResult $requestCode")
            if (requestCode == UserManagementViewModel.LOGIN_OPERATION_RESULT_CODE)
            {
                if (userManagementViewModel.processLoginResult(resultCode, data))
                {
                    Log.i("LoginActivity", "SYR -> Starting Main activity after login")
                    val intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)
                    return
                }
                else
                {
                    Log.e("LoginActivity", "SYR -> Login failed, opening again the login window")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e("LoginActivity", "SYR -> Unable to process login result ${ex.message}")
            ex.printStackTrace()
        }

        createSignInWindow()
    }
    //endregion


    /**
     * Creates the intent that shows the login window
     * This window will allow to log with mail, google and facebook
     */
    private fun createSignInWindow()
    {
        try
        {
            Log.i("LoginActivity", "SYR -> Creating login window")
            //launch the authentication activity
            val intent = userManagementViewModel.getSignInIntent()
            if (intent != null)
            {
                startActivityForResult(intent, UserManagementViewModel.LOGIN_OPERATION_RESULT_CODE)
            }
        }
        catch (ex: Exception)
        {
            Log.e("LoginActivity", "SYR -> Unable to create the login window")
            ex.printStackTrace()
        }
    }

}



