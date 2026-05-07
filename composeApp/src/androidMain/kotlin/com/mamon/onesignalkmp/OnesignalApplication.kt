package com.mamon.onesignalkmp

import android.app.Application
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import onesignal.core.initOneSignal


class OnesignalApplication : Application(){

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        initOneSignal(Constant.ONE_SIGNAL_APP_ID)

    }
}