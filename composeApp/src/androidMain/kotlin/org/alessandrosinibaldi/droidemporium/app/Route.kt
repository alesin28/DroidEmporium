package org.alessandrosinibaldi.droidemporium.app

import kotlinx.serialization.Serializable

@Serializable
sealed class Route(val path: String) {
    @Serializable
    data object StartMenu : Route("start_menu")


}