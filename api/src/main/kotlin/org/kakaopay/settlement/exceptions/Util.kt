package org.kakaopay.settlement.exceptions

import org.springframework.http.HttpStatusCode

fun InvalidCommandException.toHttpException(status: HttpStatusCode): HttpException {
    return HttpException(
        status.value(),
        this.message,
        this.className,
        this.errorProperties
    )
}

fun InvalidRequestException.toHttpException(status: HttpStatusCode): HttpException {
    return HttpException(
        status.value(),
        this.message,
        this.className,
        this.errorProperties
    )
}
