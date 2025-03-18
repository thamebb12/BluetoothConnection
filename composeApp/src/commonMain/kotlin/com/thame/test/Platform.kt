package com.thame.test

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform