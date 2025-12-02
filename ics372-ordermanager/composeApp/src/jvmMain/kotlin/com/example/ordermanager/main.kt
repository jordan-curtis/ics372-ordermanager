package com.example.ordermanager

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    val screenState = rememberWindowState(
        size = DpSize(1200.dp, 800.dp),  // <-- Much bigger!
        position = WindowPosition(100.dp, 100.dp)
    )

    Window(
        title = "Order System App",
        onCloseRequest = ::exitApplication,
        state = screenState,
        alwaysOnTop = false  // <-- Don't force on top
    ) {
        App()
    }
}