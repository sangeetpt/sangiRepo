package com.test.movies.uicomponents.ui.login.biometric

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal
import kotlin.math.abs

class KeyStoreHelper {

    private val TAG = "KeyStoreHelper"

    /**
     * Creates a public and private key and stores it using the Android Key
     * Store, so that only this application will be able to access the keys.
     */
    fun createKeys(context: Context?, alias: String) {
        try {
            if (!isSigningKey(alias)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createKeysM(alias, false)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    createKeysJBMR2(context, alias)
                }
            }
        } catch (e: NoSuchProviderException) {
            Log.e(TAG, "NoSuchProviderException: ---$e")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "NoSuchAlgorithmException: ---$e")
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, "InvalidAlgorithmParameterException: ---$e")
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class
    )
    fun createKeysJBMR2(context: Context?, alias: String) {
        val start: Calendar = GregorianCalendar()
        val end: Calendar = GregorianCalendar()
        end.add(Calendar.YEAR, 30)
        val spec =
            KeyPairGeneratorSpec.Builder(context!!) // You'll use the alias later to retrieve the key. It's a key
                // for the key!
                .setAlias(alias)
                .setSubject(X500Principal("CN=$alias"))
                .setSerialNumber(
                    BigInteger.valueOf(
                        abs(alias.hashCode()).toLong()
                    )
                ) // Date range of validity for the generated pair.
                .setStartDate(start.time).setEndDate(end.time)
                .build()
        val kpGenerator =
            KeyPairGenerator.getInstance(
                "RSA",
                "AndroidKeyStore"
            )
        kpGenerator.initialize(spec)
        val kp = kpGenerator.generateKeyPair()
        Log.d(TAG, "Public Key is: " + kp.public.toString())
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun createKeysM(alias: String?, requireAuth: Boolean) {
        try {
            val keyPairGenerator =
                KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    "AndroidKeyStore"
                )
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    alias!!,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setAlgorithmParameterSpec(
                        RSAKeyGenParameterSpec(
                            1024,
                            RSAKeyGenParameterSpec.F4
                        )
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(
                        KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512
                    ) // Only permit the private key to be used if the user authenticated
                    // within the last five minutes.
                    .setUserAuthenticationRequired(requireAuth)
                    .build()
            )
            val keyPair = keyPairGenerator.generateKeyPair()
            Log.d(TAG, "Public Key is: " + keyPair.public.toString())
        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }
    }

    /**
     * JBMR2+ If Key with the default alias exists, returns true, else false.
     * on pre-JBMR2 returns true always.
     */
    private fun isSigningKey(alias: String?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                val keyStore =
                    KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                keyStore.containsAlias(alias)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                false
            }
        } else {
            false
        }
    }

    private fun getPrivateKeyEntry(alias: String): KeyStore.PrivateKeyEntry? {
        return try {
            val ks = KeyStore
                .getInstance("AndroidKeyStore")
            ks.load(null)
            val entry = ks.getEntry(alias, null)
            if (entry == null) {
                Log.w(TAG, "No key found under alias: $alias")
                Log.w(TAG, "Exiting signData()...")
                return null
            }
            if (entry !is KeyStore.PrivateKeyEntry) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry")
                Log.w(TAG, "Exiting signData()...")
                return null
            }
            entry
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            null
        }
    }

    fun encrypt(alias: String, plaintext: String): String? {
        return try {
            val publicKey =
                getPrivateKeyEntry(alias)!!.certificate.publicKey
            val cipher = getCipher()
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            Base64.encodeToString(
                cipher.doFinal(plaintext.toByteArray()),
                Base64.NO_WRAP
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun decrypt(alias: String, ciphertext: String): String? {
        return try {
            val privateKey = getPrivateKeyEntry(alias)!!.privateKey
            val cipher = getCipher()
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            Log.e(TAG, "cipher text is:$ciphertext")
            String(
                cipher.doFinal(
                    Base64.decode(
                        ciphertext,
                        Base64.NO_WRAP
                    )
                )
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class
    )
    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            String.format(
                "%s/%s/%s",
                "RSA",
                "NONE",
                "PKCS1Padding"
            )
        )
    }


}