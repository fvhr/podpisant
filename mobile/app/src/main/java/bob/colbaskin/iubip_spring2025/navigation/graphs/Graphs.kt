package bob.colbaskin.iubip_spring2025.navigation.graphs

import kotlinx.serialization.Serializable

interface Graphs {

    @Serializable
    data object Main: Graphs

    @Serializable
    data object OnBoarding: Graphs

    @Serializable
    data object Detailed: Graphs
}