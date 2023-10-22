package org.kakaopay.settlement.exceptions

data class ErrorResponse(
    val status: Int,
    val message: String,
    val className: String,
    val errorProperties: List<ErrorProperties>? = null
)
