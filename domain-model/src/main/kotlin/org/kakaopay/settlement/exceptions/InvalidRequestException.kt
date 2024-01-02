package org.kakaopay.settlement.exceptions

data class InvalidRequestException(
    override val message: String,
    val errorProperties: List<ErrorProperties>,
    val className: String = "InvalidRequestException"
) : Error()
