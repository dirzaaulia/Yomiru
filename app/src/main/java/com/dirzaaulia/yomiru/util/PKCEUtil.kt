package com.dirzaaulia.yomiru.util

import android.util.Base64
import java.security.SecureRandom

object PKCEUtil {
    fun generateCodeVerifier(): String {
        val sr = SecureRandom()
        val code = ByteArray(32)
        sr.nextBytes(code)
        return Base64.encodeToString(
            code,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    fun generateCodeChallenge(verifier: String): String {
        // For PKCE 'plain' method, the code challenge is the same as the code verifier
        return verifier
    }
}
