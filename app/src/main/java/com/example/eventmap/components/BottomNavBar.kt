package com.example.eventmap.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eventmap.presentation.theme.ui.ButtonGray
import com.example.eventmap.presentation.theme.ui.DefaultBlue
import com.example.eventmap.presentation.theme.ui.DefaultWhite

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    showBar: Boolean = true,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStateEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStateEntry?.destination?.route
    if(showBar) {
        BottomNavigation(
            modifier = modifier,
            backgroundColor = DefaultWhite,
            elevation = 5.dp
        ) {
            items.forEach { item ->
                val selected = item.route == currentRoute
                BottomNavigationItem(
                    selected = selected,
                    onClick = { onItemClick(item) },
                    selectedContentColor = DefaultBlue,
                    unselectedContentColor = ButtonGray,
                    icon = {
                        Column(horizontalAlignment = CenterHorizontally) {
                            Icon(imageVector = item.icon, contentDescription = "icon")
                            Text(text = item.name, textAlign = TextAlign.Center, fontSize = 12.sp)
                        }
                    }
                )
            }
        }
    }
}
data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)
