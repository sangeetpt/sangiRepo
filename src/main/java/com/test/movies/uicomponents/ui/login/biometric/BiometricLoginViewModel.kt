package com.test.movies.uicomponents.ui.login.biometric

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.test.movies.utils.Util

/*
* Data binding is implemented with the biometri login layout file
* where the fields username and password are observables
 */

class BiometricLoginViewModel(application: Application) : AndroidViewModel(application) {

    private var mListener: ResultCallBack? = null
    private lateinit var utilIntance: Util
    var strUserName: ObservableField<String>? = null
    var strPassword: ObservableField<String>? = null


    init {
        strUserName = ObservableField("")
        strPassword = ObservableField("")
    }
    fun setViewListener(listener: ResultCallBack?) {
        mListener = listener
    }

    fun validateUser(username: String, password: String): Boolean {
        utilIntance = Util()
        val flag: Boolean
        if (TextUtils.isEmpty(username)) {
            flag = false
            mListener?.onError("Please enter your username!")
        } else if (!utilIntance.isValidUserName(username)) {
            flag = false
            mListener?.onError("Please enter valid username!")
        } else if (TextUtils.isEmpty(password)) {
            flag = false
            mListener?.onError("Please enter your Password!")
        } else if (!utilIntance.isValidPassword(password)) {
            flag = false
            mListener?.onError("Please enter valid password")
        } else {
            mListener?.onSuccess("Logging in...")
            flag = true
        }
        return flag
    }
}