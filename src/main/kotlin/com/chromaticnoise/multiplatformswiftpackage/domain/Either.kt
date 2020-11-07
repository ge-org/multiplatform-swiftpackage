package com.chromaticnoise.multiplatformswiftpackage.domain

internal sealed class Either<out L, out R> {
    data class Left<out L, out R>(val value: L) : Either<L, R>()
    data class Right<out L, out R>(val value: R) : Either<L, R>()

    fun <T> fold(l: (L) -> T, r: (R) -> T): T = when (this) {
        is Left -> l(value)
        is Right -> r(value)
    }

    internal companion object {
        fun <L, R> ofNullable(right: R?, left: L): Either<L, R> = when (right) {
            null -> Left(left)
            else -> Right(right)
        }
    }
}
