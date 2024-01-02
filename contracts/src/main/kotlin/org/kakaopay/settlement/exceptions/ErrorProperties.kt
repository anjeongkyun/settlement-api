package org.kakaopay.settlement.exceptions

data class ErrorProperties(
    val key: String,
    val errorReason: ErrorReason
)
