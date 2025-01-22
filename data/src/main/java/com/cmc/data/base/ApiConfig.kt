package com.cmc.data.base

import com.cmc.data.BuildConfig

internal object ApiConfig {

    var BASE_URL = if(BuildConfig.DEBUG) {
        "https://patata.kr/"
    } else {
        "https://patata.kr/"
    }
}
internal object ApiCode {
    object Auth {
        const val INVALID_GOOGLE_ID_TOKEN = "OAUTH4003"
        const val GOOGLE_ID_TOKEN_VERIFICATION_FAILED = "OAUTH4004"
    }

    object Common {
        const val GENERIC_ERROR = "ERROR400"
    }
}