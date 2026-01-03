package com.educorreia.flighthelper.features.main_page.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainPageViewModel() : ViewModel() {
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
