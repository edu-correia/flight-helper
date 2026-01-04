package com.educorreia.flighthelper.features.main_page.presentation

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educorreia.flighthelper.R
import com.educorreia.flighthelper.core.data.gateways.FlightProgressNotificationHandler
import com.educorreia.flighthelper.core.ui.theme.FlightHelperTheme

@Composable
fun MainPageScreenRoot() {
    val context = LocalContext.current
    val flightStatusNotifier by lazy { FlightProgressNotificationHandler(context.applicationContext) }
    val viewModel = MainPageViewModel(flightStatusNotifier)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MainPageEffect.ShowPostNotificationPermPopup -> {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                MainPageEffect.ShowPostPromotedNotificationPermPopup -> {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_PROMOTION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    MainPageScreen(
        onEvent = viewModel::onEvent
    )
}

@Composable
fun MainPageScreen(
    onEvent: (MainPageAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.flight_helper_main_page_title),
            fontSize = 24.sp
        )

        Button(
            onClick = { onEvent(MainPageAction.HandlePostNotificationPermission) }
        ) {
            Text(stringResource(R.string.check_post_notification_permission))
        }

        Button(
            onClick = { onEvent(MainPageAction.HandlePostPromotedNotificationPermission) }
        ) {
            Text(stringResource(R.string.check_post_promoted_notification_permission))
        }

        Button(
            onClick = { onEvent(MainPageAction.StartFlightStatusNotification) }
        ) {
            Text(stringResource(R.string.start_flight_status_notification))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPageScreenPreview() {
    FlightHelperTheme {
        MainPageScreen(
            onEvent = {}
        )
    }
}