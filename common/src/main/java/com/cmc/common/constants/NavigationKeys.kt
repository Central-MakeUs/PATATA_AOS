package com.cmc.common.constants

object NavigationKeys {

    object SpotDetail {
        const val ARGUMENT_SPOT_ID = "spotId"
    }

    object Location {
        const val ARGUMENT_IS_EDIT = "isEdit"
        const val ARGUMENT_LATITUDE = "latitude"
        const val ARGUMENT_LONGITUDE = "longitude"
        const val ARGUMENT_MIN_LATITUDE = "minLatitude"
        const val ARGUMENT_MIN_LONGITUDE = "minLongitude"
        const val ARGUMENT_MAX_LATITUDE = "maxLatitude"
        const val ARGUMENT_MAX_LONGITUDE = "maxLongitude"
    }

    object Category {
        const val ARGUMENT_CATEGORY_ID = "categoryId"
    }

    object Search {
        const val ARGUMENT_KEYWORD = "keyword"
    }

    object Map {
        const val ARGUMENT_WITH_SEARCH = "withSearch"
    }

    object Setting {
        const val ARGUMENT_PROFILE_NICKNAME = "profileNickname"
        const val ARGUMENT_PROFILE_IMAGE = "profileImage"
        const val ARGUMENT_PROFILE_EMAIL = "profileEmail"
    }

    object Report {
        const val ARGUMENT_REPORT_TYPE = "reportType"
        const val ARGUMENT_REPORT_TARGET_ID = "targetID"
    }

    object WebView {
        const val ARGUMENT_WEB_VIEW_URL = "webViewUrl"
    }
}