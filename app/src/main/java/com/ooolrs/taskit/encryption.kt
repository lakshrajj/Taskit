package com.ooolrs.lockward.db

import android.os.Build
import androidx.annotation.RequiresApi
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

class encryption {
    private val cipherTransformation = "AES/ECB/PKCS5Padding"
    private val encryptionAlgorithm = "AES"

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(input: String, secretKeys: String): String {
        var secretKey=secretKeys
        while(secretKey.length <32){
            secretKey += "1";
        }
        val key = SecretKeySpec(secretKey.toByteArray(), encryptionAlgorithm)
        val cipher = Cipher.getInstance(cipherTransformation)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(input: String, secretKeys: String): String {
        var secretKey=secretKeys
        while(secretKey.length <32){
            secretKey += "1";
        }
        val key = SecretKeySpec(secretKey.toByteArray(), encryptionAlgorithm)
        val cipher = Cipher.getInstance(cipherTransformation)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val encryptedBytes = Base64.getDecoder().decode(input)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}