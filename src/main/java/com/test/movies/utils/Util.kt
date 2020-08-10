package com.test.movies.utils

import android.text.TextUtils

class Util {

    private val instance: Util? = null

    private fun Util() {}

    fun isValidUserName(target: CharSequence?): Boolean {
        return (!TextUtils.isEmpty(target) && target!!.contains(Constants.LOGIN_USERNAME))
    }

    fun isValidPassword(target: CharSequence?): Boolean {
        return (!TextUtils.isEmpty(target) && target!!.contains(Constants.LOGIN_PASSWORD))
    }

}