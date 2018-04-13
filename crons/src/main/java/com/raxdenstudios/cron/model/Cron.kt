package com.raxdenstudios.cron.model

import android.app.AlarmManager

data class Cron(val builder: Builder) {

    val id: Long
    val type: Int
    val triggerAtTime: Long
    val interval: Long
    val status: Boolean

    init {
        id = builder.id
        type = builder.type
        triggerAtTime = builder.triggerAtTime
        interval = builder.interval
        status = builder.status
    }

    class Builder(internal var id: Long = 0) {

        internal var triggerAtTime: Long = 0
        internal var interval: Long = 0
        internal var type: Int = AlarmManager.RTC_WAKEUP
        internal var status: Boolean = true
        internal var data: String? = null

        fun id(id: Long): Builder {
            this.id = id
            return this
        }

        fun type(type: Int): Builder {
            this.type = type
            return this
        }

        fun triggerAtTime(triggerAtTime: Long): Builder {
            this.triggerAtTime = triggerAtTime
            return this
        }

        fun interval(interval: Long): Builder {
            this.interval = interval
            return this
        }

        fun status(status: Boolean): Builder {
            this.status = status
            return this
        }

        fun data(data: String): Builder {
            this.data = data
            return this
        }

        fun create(): Cron {
            return Cron(this)
        }

    }

}