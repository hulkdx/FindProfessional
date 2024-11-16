package com.hulkdx.findprofessional.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.hulkdx.findprofessional.core.config.isDebug
import com.hulkdx.findprofessional.core.navigation.NavigationScreen
import com.hulkdx.findprofessional.core.navigation.Navigator
import org.koin.compose.koinInject

/**
 *  A safe function to get navigator for previews
 */
@Composable
fun getNavigator(): Navigator {
    val isPreview = LocalInspectionMode.current
    return if (isDebug() && isPreview) {
        // @formatter:off
        object : Navigator {
            override fun navigate(screen: NavigationScreen) {}
            override fun navigate(screen: NavigationScreen, popTo: NavigationScreen, inclusive: Boolean) {}
            override fun goBack() {}
            override fun getCurrentScreen() = NavigationScreen.Login
        }
        // @formatter:on
    } else {
        koinInject()
    }
}

