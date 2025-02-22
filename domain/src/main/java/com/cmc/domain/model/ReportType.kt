package com.cmc.domain.model

enum class ReportType(val type: Int) {
    POST(0),
    USER(1);
    companion object {
        fun fromType(type: Int): ReportType {
            return entries.first { it.type == type }
        }
    }
}
