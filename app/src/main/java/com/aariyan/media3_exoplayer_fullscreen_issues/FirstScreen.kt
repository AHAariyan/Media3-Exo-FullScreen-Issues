package com.aariyan.media3_exoplayer_fullscreen_issues

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aariyan.media3_exoplayer_fullscreen_issues.ui.theme.Media3ExoPlayerFullScreenIssuesTheme


@Composable
internal fun FirstScreen(navController: NavHostController, activity: Activity, color: Int) {
    LaunchedEffect(Unit) {
        Log.d("SYSTEM_BARS", "FirstScreen: CALLED")
        activity.showSystemBars()
        activity.setStatusBarColor(color)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate("second") }) {
            Text("Go to Second Screen")
        }
    }
}


//@Composable
//internal fun FirstScreen(navController: NavHostController) {
////    val color = MaterialTheme.colorScheme.error.toArgb()
////    val activity = LocalContext.current as MainActivity
////    SideEffect {
////        activity.setStatusBarColor(color)
////    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(WindowInsets.systemBars.asPaddingValues()),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(onClick = { navController.navigate("second") }) {
//            Text("Go to Second Screen")
//        }
//    }
//}

@Preview
@Composable
internal fun PreviewFirstScreen() {

    val navController = rememberNavController()

    Media3ExoPlayerFullScreenIssuesTheme {
       // FirstScreen(navController = navController)
    }
}