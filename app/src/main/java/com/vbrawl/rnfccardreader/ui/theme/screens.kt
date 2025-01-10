package com.vbrawl.rnfccardreader.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vbrawl.rnfccardreader.MainActivity

@Composable
fun MainUI(activity: MainActivity) {
    val navController = rememberNavController()

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        if (destination.route == "main")
            { activity.sock?.connect(activity.url) }
        else { activity.sock?.disconnect() }
    }

    NavHost(
        navController=navController,
        startDestination="main"
    ) {
        composable("main") { MainScreen(navController) }
        composable("settings") { SettingsScreen(activity, navController) }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    RNFCCardReaderTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            navController.navigate("settings")
                        }
                    )
                }
        ) {
            Text(
                text = "NFC Module!"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(activity: MainActivity, navController: NavController) {
    var hostUrl by remember { mutableStateOf(activity.url) }

    RNFCCardReaderTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            contentPadding = PaddingValues(1.dp),
                            modifier = Modifier
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {

                        OutlinedButton(
                            onClick = {
                                activity.url = MainActivity.URL_DEFAULT
                                hostUrl = MainActivity.URL_DEFAULT
                            },
                            contentPadding = PaddingValues(1.dp),
                            border = BorderStroke(0.dp, Color.Transparent),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Defaults",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = hostUrl,
                    label = {
                        Text(text="Host")
                    },
                    singleLine = true,
                    onValueChange = {
                        hostUrl = it
                        activity.url = it
                    }
                )
            }
        }


    }
}