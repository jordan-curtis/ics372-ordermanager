package com.example.ordermanager

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import java.io.FileFilter

fun main() = application {
    DirectoryThread.runThread();

    val screenState = rememberWindowState(size = DpSize(1000.dp, 700.dp),
        position = WindowPosition(300.dp, 300.dp))


    Window(
        title = "Order System App",
        onCloseRequest = ::exitApplication,
        state = screenState,
        alwaysOnTop = true
    ) {
        App()
    }

    DisposableEffect(Unit) {
        onDispose {
            // Code to run on app close
            DirectoryThread.shouldRun = false;
        }
    }
}