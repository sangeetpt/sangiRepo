package com.test.movies.uicomponents.ui.login.biometric

/*
*  Result Callback interface used to show success and error message
 */
interface ResultCallBack {
    fun onSuccess(s: String?)
    fun onError(s: String?)
}