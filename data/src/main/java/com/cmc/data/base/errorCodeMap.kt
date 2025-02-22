package com.cmc.data.base

import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.base.exception.AppInternalException

val errorCodeMap = mapOf(
    ApiCode.Common.GENERIC_ERROR to { msg: String, _: Any? ->  ApiException.BadRequest(msg) },

    ApiCode.Auth.INVALID_GOOGLE_ID_TOKEN to { msg: String, _: Any? ->  ApiException.NotFound(msg) },
    ApiCode.Auth.GOOGLE_ID_TOKEN_VERIFICATION_FAILED to { msg: String, data: Any? ->  AppInternalException.PermissionDenied(msg, data) },
    ApiCode.Auth.ACCESS_TOKEN_EXPIRED to { msg: String, data: Any? ->  ApiException.AccessTokenExpired(msg, data) },

    ApiCode.Member.MEMBER_NOT_FOUND to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },
    ApiCode.Member.MEMBER_NICKNAME_ALREADY_IN_USE to { msg: String, data: Any? ->  ApiException.BadRequest(msg, data) },
    ApiCode.Member.MEMBER_MISMATCH to { msg: String, data: Any? ->  ApiException.BadRequest(msg, data) },
    ApiCode.Member.MEMBER_DELETION_FAILED to { msg: String, data: Any? ->  ApiException.ServerError(msg, data) },
    ApiCode.Member.MEMBER_DELETED_ACCOUNT to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },

    ApiCode.Spot.SPOT_NOT_FOUND to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },
    ApiCode.Spot.SPOT_SEARCH_NO_RESULT to { msg: String, _: Any? ->  ApiException.NotFound(msg) },
    ApiCode.Spot.SPOT_TOO_MANY_REGISTERED to { msg: String, data: Any? ->  ApiException.RegistrationLimitExceeded(msg, data) },
    ApiCode.Spot.SPOT_CATEGORY_NOT_FOUND to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },
    ApiCode.Spot.SPOT_ACCESS_DENIED to { msg: String, data: Any? ->  AppInternalException.PermissionDenied(msg, data) },
    ApiCode.Spot.SPOT_INVALID_SORT_PARAMETER to { msg: String, data: Any? ->  ApiException.BadRequest(msg, data) },
    ApiCode.Spot.SPOT_TOO_MANY_REGISTERED to { msg: String, data: Any? ->  ApiException.BadRequest(msg, data) },
    ApiCode.Spot.SPOT_SEARCH_NO_RESULT to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },
    ApiCode.Spot.SPOT_UPLOAD_FAIL to { msg: String, data: Any? ->  ApiException.ServerError(msg, data) },

    ApiCode.Scrap.SCRAP_FAILED to { msg: String, data: Any? ->  AppInternalException.UnknownError(msg, data) },

    ApiCode.Review.REVIEW_NOT_FOUND to { msg: String, data: Any? ->  ApiException.NotFound(msg, data) },
    ApiCode.Review.REVIEW_NOT_AUTHOR to { msg: String, data: Any? ->  AppInternalException.PermissionDenied(msg, data) },

    ApiCode.Image.IMAGE_NOT_ATTACHED to { msg: String, data: Any? ->  ApiException.BadRequest(msg, data) },
    ApiCode.Image.IMAGE_FAILED_UPLOAD to { msg: String, data: Any? ->  ApiException.ServerError(msg, data) },
)