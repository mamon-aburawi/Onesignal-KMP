package com.mamon.onesignalkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform