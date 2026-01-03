package com.educorreia.flighthelper.core.data.domain.interfaces

import com.educorreia.flighthelper.core.data.domain.models.enums.FlightStatus

interface FlightStatusNotifier {
    fun notifyFlightStatus(flightStatus: FlightStatus)
}