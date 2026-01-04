package com.educorreia.flighthelper.core.data.domain.interfaces

import com.educorreia.flighthelper.core.data.domain.models.enums.FlightStatus
import kotlinx.coroutines.flow.Flow

interface FlightStatusProvider {
    fun getFlightStatusUpdates(): Flow<FlightStatus>
}