package com.educorreia.flighthelper.features.main_page.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educorreia.flighthelper.core.data.domain.interfaces.FlightStatusNotifier
import com.educorreia.flighthelper.core.data.domain.interfaces.FlightStatusProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainPageViewModel(
    private val statusNotifier: FlightStatusNotifier,
    private val statusProvider: FlightStatusProvider
) : ViewModel() {
    var effect = MutableSharedFlow<MainPageEffect>()
        private set

    fun onEvent(action: MainPageAction) {
        when (action) {
            is MainPageAction.HandlePostNotificationPermission -> {
                handlePostNotificationPermission()
            }
            is MainPageAction.HandlePostPromotedNotificationPermission -> {
                handlePostPromotedNotificationPermission()
            }
            is MainPageAction.StartFlightStatusNotification -> {
                viewModelScope.launch {
                    statusProvider.getFlightStatusUpdates().collect { status ->
                        statusNotifier.notifyFlightStatus(status)
                    }
                }
            }
        }
    }

    private fun handlePostNotificationPermission() {
        viewModelScope.launch {
            effect.emit(MainPageEffect.ShowPostNotificationPermPopup)
        }
    }

    private fun handlePostPromotedNotificationPermission() {
        viewModelScope.launch {
            effect.emit(MainPageEffect.ShowPostPromotedNotificationPermPopup)
        }
    }
}
