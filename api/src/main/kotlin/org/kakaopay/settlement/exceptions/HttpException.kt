package org.kakaopay.settlement.exceptions

data class HttpException(
    val status: Int,
    override val message: String,
    val className: String,
    val errorProperties: List<ErrorProperties>
) : Error()
