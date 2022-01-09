package com.acanel.deskclock.ui

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acanel.deskclock.ui.screen.ClockScreen
import com.acanel.deskclock.ui.screen.SettingScreen

object DeskClockDestination {
    const val CLOCK = "clock"
    const val SETTING = "setting"
}
val LocalNavAction = compositionLocalOf<DeskClockAction> { error("can't find Action") }

@Composable
fun DeskClockNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = DeskClockDestination.CLOCK) {
        composable(DeskClockDestination.CLOCK) {
            ClockScreen()
        }
        composable(DeskClockDestination.SETTING) {
            SettingScreen()
        }
    }
}

class DeskClockAction(private val activity: Activity, private val navController: NavHostController?) {
    val clockAction: () -> Unit = {
        navController?.navigate(DeskClockDestination.CLOCK)
    }
    val settingAction: () -> Unit = {
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
    val backAction: () -> Unit = {
        navController?.popBackStack()
    }

    val finishAction: () -> Unit = {
        activity.finish()
    }
}