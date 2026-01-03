package com.educorreia.flighthelper.features.main_page.presentation

sealed interface MainPageEffect {
    object ShowPostNotificationPermPopup: MainPageEffect
    object ShowPostPromotedNotificationPermPopup: MainPageEffect
}