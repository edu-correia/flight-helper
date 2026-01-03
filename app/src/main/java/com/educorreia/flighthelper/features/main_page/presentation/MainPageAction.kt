package com.educorreia.flighthelper.features.main_page.presentation

sealed interface MainPageAction {
    object HandlePostNotificationPermission : MainPageAction
    object HandlePostPromotedNotificationPermission : MainPageAction
}