package onesignal.feature_scheduler.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import onesignal.feature_scheduler.domain.model.OneSignalScheduleConfig
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

object OneSignalScheduler {

    fun scheduleAfter(delay: Duration): OneSignalScheduleConfig {
        val futureTime = Clock.System.now() + delay
        return createConfigFromInstant(futureTime)
    }

    fun scheduleAfter(delayInMillis: Long): OneSignalScheduleConfig {
        return scheduleAfter(delayInMillis.milliseconds)
    }

    fun scheduleAt(dateInMilliSeconds: Long, localHour: Int, localMinute: Int): OneSignalScheduleConfig? {

        val date = Instant.Companion.fromEpochMilliseconds(dateInMilliSeconds).toLocalDateTime(
            TimeZone.Companion.currentSystemDefault()).date

        val localDateTime = LocalDateTime(
            year = date.year,
            month = date.month.number,
            day = date.day,
            hour = localHour,
            minute = localMinute,
            second = 0,
            nanosecond = 0
        )


        val targetInstant = localDateTime.toInstant(TimeZone.Companion.currentSystemDefault())


        if (targetInstant <= Clock.System.now()) {
            return null
        }

        return createConfigFromInstant(targetInstant)
    }

    private fun createConfigFromInstant(instant: Instant): OneSignalScheduleConfig {
        val utcDateTime = instant.toLocalDateTime(TimeZone.Companion.UTC)

        val formattedTime = with(utcDateTime) {
            val y = year.toString().padStart(4, '0')
            val m = month.number.toString().padStart(2, '0')
            val d = day.toString().padStart(2, '0')
            val h = hour.toString().padStart(2, '0')
            val min = minute.toString().padStart(2, '0')
            val s = second.toString().padStart(2, '0')

            "$y-$m-$d $h:$min:$s GMT-0000"
        }

        return OneSignalScheduleConfig(
            sendAfter = formattedTime,
            delayedOption = null,
            deliveryTimeOfDay = null
        )
    }
}