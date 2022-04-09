package com.acanel.deskclock.ui

import android.app.Activity
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acanel.deskclock.ui.screen.ClockActivityScreen
import com.acanel.deskclock.ui.screen.SettingActivityScreen

object DeskClockDestination {
    const val CLOCK = "clock"
    const val SETTING = "setting"
}
val LocalNavAction = compositionLocalOf<DeskClockNavAction> { MockDeskClockNavActionImpl() }

@Composable
fun DeskClockNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = DeskClockDestination.CLOCK) {
        composable(DeskClockDestination.CLOCK) {
            ClockActivityScreen()
        }
        composable(DeskClockDestination.SETTING) {
            SettingActivityScreen()
        }
    }
}

interface DeskClockNavAction {
    val clockAction: () -> Unit
    val settingAction: () -> Unit
    val backAction: () -> Unit
    val finishAction: () -> Unit
}

class DeskClockNavActionImpl(private val activity: Activity, private val navController: NavHostController?): DeskClockNavAction {
    override val clockAction: () -> Unit = {
        navController?.navigate(DeskClockDestination.CLOCK)
    }
    override val settingAction: () -> Unit = {
        navController?.navigate(DeskClockDestination.SETTING)
    }
    /*
    val schemeAction: (String) -> Unit = { url ->
        val uri: Uri = Uri.parse(url)
        if ("groovin" == uri.scheme) {
            navController?.navigate(uri.host!!)
        }
    }
    */
    override val backAction: () -> Unit = {
        navController?.popBackStack()
    }

    override val finishAction: () -> Unit = {
        activity.finish()
    }
}

class MockDeskClockNavActionImpl: DeskClockNavAction {
    override val clockAction: () -> Unit = {}
    override val settingAction: () -> Unit = {}
    override val backAction: () -> Unit = {}
    override val finishAction: () -> Unit = {}
}