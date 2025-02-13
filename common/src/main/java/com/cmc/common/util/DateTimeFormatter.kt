package com.example.common.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatterUtil {

    private const val INPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private const val OUTPUT_FORMAT = "yy.MM.dd  HH:mm"

    /**
     * UTC 시간을 입력받아 yy.MM.dd  HH:mm 형식으로 반환
     * @param utcDateTime UTC 시간 문자열 (예: 2024-07-10T00:48:00Z)
     * @return 변환된 문자열 (예: 24.07.10  00:48)
     */
    fun formatUtcToLocal(utcDateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")  // 입력은 UTC
            }
            val date = inputFormat.parse(utcDateTime) ?: return "25.01.01  00:00"

            val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()  // 출력은 로컬 시간
            }
            outputFormat.format(date)
        } catch (e: Exception) {
            "25.01.01  00:00"
        }
    }
}
