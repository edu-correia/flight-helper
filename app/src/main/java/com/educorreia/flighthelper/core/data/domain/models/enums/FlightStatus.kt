package com.educorreia.flighthelper.core.data.domain.models.enums

sealed interface FlightStatus {
    data class CheckIn(val destinationName: String, val checkInDeadline: String) : FlightStatus
    data class GateOpen(val gateName: String, val gateOpenDeadline: String) : FlightStatus
    data class InTheAir(val originName: String, val destinationName: String, val estimatedArrival: String) : FlightStatus
    data class BaggageClaim(val baggageCarouselName: String) : FlightStatus
    data class Finished(val destinationName: String) : FlightStatus
}