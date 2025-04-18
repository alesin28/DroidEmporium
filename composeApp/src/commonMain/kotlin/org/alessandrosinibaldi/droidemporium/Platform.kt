package org.alessandrosinibaldi.droidemporium

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform