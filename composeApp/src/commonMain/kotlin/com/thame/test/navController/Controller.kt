package com.thame.test.navController

import bluetoothconnection.composeapp.generated.resources.Res
import bluetoothconnection.composeapp.generated.resources.chat
import bluetoothconnection.composeapp.generated.resources.home
import bluetoothconnection.composeapp.generated.resources.host
import org.jetbrains.compose.resources.StringResource

enum class Controller (val page:StringResource){
    Home(page = Res.string.home),
    Host(page = Res.string.host),
    Chat(page = Res.string.chat)
}