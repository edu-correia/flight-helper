package com.educorreia.flighthelper.core.data.gateways

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.ProgressStyle
import androidx.core.graphics.drawable.IconCompat
import com.educorreia.flighthelper.MainActivity
import com.educorreia.flighthelper.R
import com.educorreia.flighthelper.core.data.domain.interfaces.FlightStatusNotifier
import com.educorreia.flighthelper.core.data.domain.models.enums.FlightStatus
import com.educorreia.flighthelper.core.ui.theme.Pink70

class FlightProgressNotificationHandler(
    private val context: Context
) : FlightStatusNotifier {
    private var notificationManager: NotificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val CHANNEL_NAME = context.getString(R.string.flight_status_channel_name)

    init {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    private companion object {
        // Notification details
        const val CHANNEL_ID = "flight_status_channel_id"
        const val NOTIFICATION_ID = 1234

        // Progress details
        val NUMBER_OF_SEGMENTS = 5
        const val PROGRESS_MAX = 100
    }

    override fun notifyFlightStatus(flightStatus: FlightStatus) {
        val notificationBuilder = buildBaseNotification()

        when (flightStatus) {
            is FlightStatus.CheckIn -> {
                notificationBuilder
                    .setContentTitle(
                        context.getString(R.string.check_in_status_title,
                            flightStatus.destinationName)
                    )
                    .setContentText(
                        context.getString(R.string.check_in_status_subtitle,
                            flightStatus.checkInDeadline)
                    )
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null,
                            context.getString(R.string.view_boarding_pass_action_label),
                            createPendingIntent("VIEW_BOARDING_PASS")
                        ).build()
                    )
            }

            is FlightStatus.GateOpen -> {
                notificationBuilder
                    .setContentTitle(
                        context.getString(R.string.gate_open_status_title,
                            flightStatus.gateName)
                    )
                    .setContentText(
                        context.getString(R.string.gate_open_status_subtitle,
                        flightStatus.gateOpenDeadline)
                    )
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null,
                            context.getString(R.string.view_airport_map_action_label),
                            createPendingIntent("VIEW_AIRPORT_MAP")
                        ).build()
                    )
            }

            is FlightStatus.InTheAir -> {
                notificationBuilder
                    .setContentTitle(
                        context.getString(R.string.in_the_air_status_title,
                            flightStatus.originName, flightStatus.destinationName)
                    )
                    .setContentText(
                        context.getString(R.string.in_the_air_status_subtitle,
                            flightStatus.estimatedArrival)
                    )
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
            }

            is FlightStatus.BaggageClaim -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.baggage_claim_status_title))
                    .setContentText(
                        context.getString(R.string.baggage_claim_status_subtitle,
                        flightStatus.baggageCarouselName)
                    )
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null,
                            context.getString(R.string.baggage_claim_info_action_label),
                            createPendingIntent("BAGGAGE_CLAIM")
                        ).build()
                    )
            }

            is FlightStatus.Finished -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.finished_status_title))
                    .setContentText(context.getString(R.string.finished_status_subtitle))
                    .setStyle(
                        buildBaseProgressStyle()
                            .setProgressTrackerIcon(getProgressIcon(flightStatus))
                            .setProgress(100)
                    )
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null,
                            context.getString(R.string.rate_experience_action_label),
                            createPendingIntent("RATE_EXPERIENCE")
                        ).build()
                    )
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null,
                            context.getString(R.string.explore_city_action_label,
                                flightStatus.destinationName),
                            createPendingIntent("EXPLORE_CITY")
                        ).build()
                    )
            }
        }

        val notification = notificationBuilder.build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildBaseNotification(): NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setRequestPromotedOngoing(true)

        return notificationBuilder
    }

    private fun buildBaseProgressStyle(): ProgressStyle {
        val segmentColor = Pink70.toArgb()

        val segments = List(NUMBER_OF_SEGMENTS) {
            ProgressStyle.Segment(PROGRESS_MAX / NUMBER_OF_SEGMENTS).setColor(segmentColor)
        }

        return ProgressStyle().setProgressSegments(segments)
    }

    private fun getMiddleOfTheSegmentStyle(flightStatus: FlightStatus): ProgressStyle {
        return buildBaseProgressStyle()
            .setProgressTrackerIcon(getProgressIcon(flightStatus))
            .setProgress(getProgressValue(flightStatus))
    }

    private fun getProgressIcon(flightStatus: FlightStatus): IconCompat {
        val iconRes = when (flightStatus) {
            is FlightStatus.CheckIn -> R.drawable.ic_ticket
            is FlightStatus.GateOpen -> R.drawable.ic_doors
            is FlightStatus.InTheAir -> R.drawable.ic_airplane
            is FlightStatus.BaggageClaim -> R.drawable.ic_luggage
            is FlightStatus.Finished -> R.drawable.ic_check
        }

        return IconCompat.createWithResource(context, iconRes)
    }

    private fun getProgressValue(flightStatus: FlightStatus): Int {
        val statusOrder = when (flightStatus) {
            is FlightStatus.CheckIn -> 0
            is FlightStatus.GateOpen -> 1
            is FlightStatus.InTheAir -> 2
            is FlightStatus.BaggageClaim -> 3
            is FlightStatus.Finished -> 4
        }

        return (statusOrder * (100 / NUMBER_OF_SEGMENTS)) + ((100 / NUMBER_OF_SEGMENTS) / 2)
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("FLIGHT_STATUS_ACTION", action)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

}