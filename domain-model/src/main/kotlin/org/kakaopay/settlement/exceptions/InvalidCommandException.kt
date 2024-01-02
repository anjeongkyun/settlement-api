package org.kakaopay.settlement.exceptions

data class InvalidCommandException(
    override val message: String,
    val errorProperties: List<ErrorProperties>,
    val className: String = "InvalidCommandException"
) : Error()
