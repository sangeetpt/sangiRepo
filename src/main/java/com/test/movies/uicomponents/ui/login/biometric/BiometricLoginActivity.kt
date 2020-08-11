package com.test.movies.uicomponents.ui.login.biometric

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.biometric.BiometricPrompt
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.lifecycle.ViewModelProviders
import com.test.movies.BaseActivity
import com.test.movies.R
import com.test.movies.uicomponents.MovieListActivity
import com.test.movies.utils.Constants
import kotlinx.android.synthetic.main.activity_biometric_log.*
import java.util.concurrent.Executors


private lateinit var keyStoreHelper: KeyStoreHelper
var biometricLogin: String? = null
var firstTimeLogin: Boolean = true
private lateinit var biologinViewModel: BiometricLoginViewModel

/*
* This activity displays a login screen saving credentials to biometric keystore
* enabling auto login further
 */
class BiometricLoginActivity : BaseActivity(), ResultCallBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric_log)


        checkBiometric()
        checkForFirstTimeLogin()
        loadViewModel()
        onclickEvents()
    }

    private fun onclickEvents() {
        login.setOnClickListener { v ->
            clickLogin()
        }

        val touchID = findViewById<SwitchCompat>(R.id.touchID)
        touchID.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                loadBiometricPrompt()
            }
        }
    }

    private fun clickLogin() {
        if (biologinViewModel
                .validateUser(username.text.toString(), password.text.toString())
        ) {
            launchNextPage()
        }
    }

    private fun loadBiometricPrompt() {

        keyStoreHelper = KeyStoreHelper()

        val executor = Executors.newSingleThreadExecutor()
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricLogin = "success"
                    saveBiometricKey()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    println("fingerprint login failed")
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(resources.getString(R.string.bioprompt_title))
            .setSubtitle(resources.getString(R.string.bioprompt_sub_title))
            .setDescription(resources.getString(R.string.bioprompt_desc))
            .setNegativeButtonText(resources.getString(R.string.bioprompt_negative_btn))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun checkBiometric() {
        if (isAvailable()) {
            login.visibility = View.GONE
            touchID.visibility = View.VISIBLE
        } else {
            login.visibility = View.VISIBLE
            touchID.visibility = View.GONE
            login.isEnabled = true
        }
    }

    private fun checkForFirstTimeLogin() {
        val sharedpreferences = getSharedPreferences(
            Constants.SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()

        firstTimeLogin = if (sharedpreferences.getBoolean(Constants.LOGIN_FIRSTIME, true)) {
            editor.putBoolean(Constants.LOGIN_FIRSTIME, false).apply()
            true
        } else {
            false
        }
    }

    private fun loadViewModel() {
        biologinViewModel = ViewModelProviders.of(this@BiometricLoginActivity).get(
            BiometricLoginViewModel::class.java
        )
        biologinViewModel.setViewListener(this@BiometricLoginActivity)
    }


    private fun isAvailable(): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(this)
        return fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
    }

    private fun setUserName() {
        keyStoreHelper.createKeys(this@BiometricLoginActivity, Constants.KEYSTORE_ENCRPT_UNAME)

        val sharedpreferences = getSharedPreferences(
            Constants.SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.putString(
            Constants.UNAME_PREF,
            keyStoreHelper.encrypt(Constants.KEYSTORE_ENCRPT_UNAME, username.text.toString())
        )
        editor.apply()
    }

    private fun setPassword() {
        keyStoreHelper.createKeys(this@BiometricLoginActivity, Constants.KEYSTORE_ENCRPT_PWD)
        val sharedpreferences = getSharedPreferences(
            Constants.SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.putString(
            Constants.PWD_PREF,
            keyStoreHelper.encrypt(Constants.KEYSTORE_ENCRPT_PWD, username.text.toString())
        )
        editor.apply()
    }

    private fun getUserName(): String? {
        keyStoreHelper.createKeys(this@BiometricLoginActivity, Constants.KEYSTORE_ENCRPT_UNAME)
        val sharedpreferences = getSharedPreferences(
            Constants.SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return keyStoreHelper.decrypt(
            Constants.KEYSTORE_ENCRPT_UNAME,
            sharedpreferences.getString(Constants.UNAME_PREF, "")!!
        )
    }

    private fun getPassword(): String? {
        keyStoreHelper.createKeys(this@BiometricLoginActivity, Constants.KEYSTORE_ENCRPT_PWD)
        val sharedpreferences = getSharedPreferences(
            Constants.SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return keyStoreHelper.decrypt(
            Constants.KEYSTORE_ENCRPT_PWD,
            sharedpreferences.getString(Constants.PWD_PREF, "")!!
        )
    }

    fun saveBiometricKey() {
        if (biometricLogin.equals("success")) {
            if (firstTimeLogin) {
                if (biologinViewModel.validateUser(
                        username.text.toString(),
                        password.text.toString()
                    )
                ) {
                    keyStoreHelper.createKeys(
                        this@BiometricLoginActivity,
                        Constants.KEYSTORE_ENCRPT_UNAME
                    )
                    setUserName()
                    keyStoreHelper.createKeys(
                        this@BiometricLoginActivity,
                        Constants.KEYSTORE_ENCRPT_PWD
                    )
                    setPassword()
                    launchNextPage()
                }
            } else {
                biologinViewModel.strUserName?.set(getUserName())
                biologinViewModel.strPassword?.set(getPassword())
                    launchNextPage()
            }
        }
    }

    private fun launchNextPage() {
        //open movie list activity here
        finish()
        val intent = Intent(this, MovieListActivity::class.java)
        startActivity(intent)
    }

    override fun onSuccess(msg: String?) {
        runOnUiThread {
            Toast .makeText(applicationContext, msg, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onError(msg: String?) {
        runOnUiThread {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
                .show()
        }
    }

}
