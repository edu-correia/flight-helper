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
        val NUMBER_OF_SEGMENTS = FlightStatus.entries.size
        const val PROGRESS_MAX = 100
    }

    override fun notifyFlightStatus(flightStatus: FlightStatus) {
        val notificationBuilder = buildBaseNotification()

        when (flightStatus) {
            FlightStatus.CHECK_IN -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.check_in_status_title))
                    .setContentText(context.getString(R.string.check_in_status_subtitle))
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null, context.getString(R.string.view_boarding_pass_action_label),
                            createPendingIntent("VIEW_BOARDING_PASS")
                        ).build()
                    )
            }

            FlightStatus.GATE_OPEN -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.gate_open_status_title))
                    .setContentText(context.getString(R.string.gate_open_status_subtitle))
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null, context.getString(R.string.view_airport_map_action_label),
                            createPendingIntent("VIEW_AIRPORT_MAP")
                        ).build()
                    )
            }

            FlightStatus.IN_THE_AIR -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.in_the_air_status_title))
                    .setContentText(context.getString(R.string.in_the_air_status_subtitle))
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
            }

            FlightStatus.BAGGAGE_CLAIM -> {
                notificationBuilder
                    .setContentTitle(context.getString(R.string.baggage_claim_status_title))
                    .setContentText(context.getString(R.string.baggage_claim_status_subtitle))
                    .setStyle(getMiddleOfTheSegmentStyle(flightStatus))
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null, context.getString(R.string.baggage_claim_info_action_label),
                            createPendingIntent("BAGGAGE_CLAIM")
                        ).build()
                    )
            }

            FlightStatus.FINISHED -> {
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
                            null, context.getString(R.string.rate_experience_action_label),
                            createPendingIntent("RATE_EXPERIENCE")
                        ).build()
                    )
                    .addAction(
                        NotificationCompat.Action.Builder(
                            null, context.getString(R.string.explore_city_action_label),
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
            FlightStatus.CHECK_IN -> R.drawable.ic_ticket
            FlightStatus.GATE_OPEN -> R.drawable.ic_doors
            FlightStatus.IN_THE_AIR -> R.drawable.ic_airplane
            FlightStatus.BAGGAGE_CLAIM -> R.drawable.ic_luggage
            FlightStatus.FINISHED -> R.drawable.ic_check
        }

        return IconCompat.createWithResource(context, iconRes)
    }

    private fun getProgressValue(flightStatus: FlightStatus): Int {
        return (flightStatus.ordinal * (100 / NUMBER_OF_SEGMENTS)) + ((100 / NUMBER_OF_SEGMENTS) / 2)
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