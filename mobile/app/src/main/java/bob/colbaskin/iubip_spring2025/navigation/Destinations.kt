package bob.colbaskin.iubip_spring2025.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

enum class Destinations(
    val icon: ImageVector,
    val label: String,
    val screen: Screens
) {
    ORGANIZATIONS(
        icon = Icons.Default.Business,
        label = "Организации",
        screen = Screens.Organizations
    ),
    DOCUMENTS(
        icon = Icons.Default.Folder,
        label = "Документы",
        screen = Screens.Documents
    ),
    PROFILE(
        icon = Icons.Default.Person,
        label = "Профиль",
        screen = Screens.Profile(id = UUID.randomUUID().toString())
    )
}