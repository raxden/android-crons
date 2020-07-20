package com.raxdenstudios.cron

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.raxdenstudios.cron.utils.CronUtils

class CronManager private constructor(builder: Builder) {

    class Builder {

        fun build(): CronManager = CronManager(this)
    }

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity?) {}

            override fun onActivityResumed(activity: Activity?) {
                CronUtils.isForeground = true
            }

            override fun onActivityPaused(activity: Activity?) {
                CronUtils.isForeground = false
            }

            override fun onActivityStopped(activity: Activity?) {}

            override fun onActivityDestroyed(activity: Activity?) {}

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
        })
    }
}