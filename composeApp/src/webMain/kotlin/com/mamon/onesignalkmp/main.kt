package com.mamon.onesignalkmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.coroutines.DelicateCoroutinesApi
import onesignal.core.initOneSignal


@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
fun main() {
    ComposeViewport {
        initOneSignal(Constant.ONE_SIGNAL_APP_ID)

        App()
    }
}