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
    object Common {
        const val GENERIC_ERROR = "ERROR400"
    }

    object Auth {
        const val ACCESS_TOKEN_EXPIRED = "OAUTH4001"
        const val INVALID_GOOGLE_ID_TOKEN = "OAUTH4003"
        const val GOOGLE_ID_TOKEN_VERIFICATION_FAILED = "OAUTH4004"
    }

    object Spot {
        const val SPOT_NOT_FOUND = "SPOT4000"
        const val SPOT_CATEGORY_NOT_FOUND = "SPOT4001"
        const val SPOT_ACCESS_DENIED = "SPOT4002"
        const val SPOT_INVALID_SORT_PARAMETER = "SPOT4003"
        const val SPOT_TOO_MANY_REGISTERED = "SPOT4004"
        const val SPOT_SEARCH_NO_RESULT = "SPOT4005"
    }

    object Scrap {
        const val SCRAP_FAILED = "SCRAP4000"
    }

    object Member {
        const val MEMBER_NOT_FOUND = "MEMBER4000"
        const val MEMBER_NICKNAME_ALREADY_IN_USE = "MEMBER4001"
        const val MEMBER_MISMATCH = "MEMBER4002"
        const val MEMBER_DELETION_FAILED = "MEMBER4003"
    }

    object Review {
        const val REVIEW_NOT_FOUND = "REVIEW4000"
        const val REVIEW_NOT_AUTHOR = "REVIEW4001"
    }
}