package com.educorreia.flighthelper.core.data.gateways

import com.educorreia.flighthelper.core.data.domain.interfaces.FlightStatusProvider
import com.educorreia.flighthelper.core.data.domain.models.enums.FlightStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockFlightStatusProvider : FlightStatusProvider {
    override fun getFlightStatusUpdates(): Flow<FlightStatus> = flow {
        emit(FlightStatus.CheckIn("Amsterdam", "10:15 AM"))
        delay(5000)
        emit(FlightStatus.GateOpen("B12", "11:30 AM"))
        delay(5000)
        emit(FlightStatus.InTheAir("Berlin", "Amsterdam", "1:30 PM"))
        delay(5000)
        emit(FlightStatus.BaggageClaim("AA123"))
        delay(5000)
        emit(FlightStatus.Finished("Amsterdam"))
    }
}