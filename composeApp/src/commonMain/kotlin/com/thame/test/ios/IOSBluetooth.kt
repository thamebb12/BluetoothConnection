package com.thame.test.ios

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("CallIOS", exact = true)
interface CallIOS{
    fun startPeripheral()
    fun stopPeripheral()
}