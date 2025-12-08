package com.example.ordermanager

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File

fun main() = application {
    //Load any previous saved data on startup
    val stateFile = File("data/state.json")
    if(stateFile.exists()){
        OrderManager.loadState(stateFile)
        println("Loaded previous save data")
    }

    // Start directory polling thread after OrderManager state is loaded
    DirectoryThread.runThread();

    val screenState = rememberWindowState(size = DpSize(1200.dp, 800.dp),
        position = WindowPosition(300.dp, 300.dp))

    Window(
        title = "Order System App",
        onCloseRequest = {
            //Now saves before closing
            OrderManager.saveState(stateFile)
            println("Saved your file before exiting")
            exitApplication()
        },
        state = screenState,
        alwaysOnTop = true
    ) {
        App()
    }

    // Stop directory polling before app is closed
    DisposableEffect(Unit) {
        onDispose {
            // Code to run on app close
            DirectoryThread.shouldRun = false;
        }
    }
}