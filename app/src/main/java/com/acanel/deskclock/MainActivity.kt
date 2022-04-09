package com.acanel.deskclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.acanel.deskclock.ui.DeskClockNavAction
import com.acanel.deskclock.ui.DeskClockNavActionImpl
import com.acanel.deskclock.ui.DeskClockNavGraph
import com.acanel.deskclock.ui.LocalNavAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navAction: DeskClockNavAction by remember { mutableStateOf(DeskClockNavActionImpl(this, navController)) }
            CompositionLocalProvider(LocalNavAction provides navAction) {
                DeskClockNavGraph(navController)
            }
        }
    }
}
