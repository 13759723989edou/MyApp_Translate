package com.androidstudio_2024_vision.myapp

import java.security.MessageDigest
// MD5工具类
object MD5Utils {
    fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray(Charsets.UTF_8))
            digest.fold("", { str, it -> str + "%02x".format(it) })
        } catch (e: Exception) {
            "MD5加密异常"
        }
    }
}