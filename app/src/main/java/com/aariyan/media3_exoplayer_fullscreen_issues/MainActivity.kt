package com.aariyan.media3_exoplayer_fullscreen_issues

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aariyan.media3_exoplayer_fullscreen_issues.ui.theme.Media3ExoPlayerFullScreenIssuesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force landscape orientation
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Enable fullscreen mode
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

//        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        //WindowCompat.setDecorFitsSystemWindows(window, true)


        setContent {
            Media3ExoPlayerFullScreenIssuesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    //ExoPlayerUi()
                    //FirstScreen()
                    AppNavigation()
                }
            }
        }
    }
}

fun Activity.setStatusBarColor(color: Int) {
    window.statusBarColor = color
}

fun Activity.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
}

@Composable
internal fun AppNavigation() {
    val navController = rememberNavController()
    val activity = LocalContext.current as Activity
    val color = MaterialTheme.colorScheme.error.toArgb()

    NavHost(navController, startDestination = "first") {
        composable("first") {
            FirstScreen(navController, activity, color)
        }
        composable("second") {
            SecondScreen(navController, activity, color)
        }
        composable("fullscreen") {
            LaunchedEffect(Unit) {
                activity.hideSystemBars()
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            FullScreenPlayer()
        }
    }
}


fun Context.findActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}


//@Composable
//internal fun AppNavigation() {
//    val navController = rememberNavController()
//    val activity = LocalContext.current as MainActivity
//    val color = MaterialTheme.colorScheme.error.toArgb()
//
//    NavHost(navController, startDestination = "first") {
//        composable("first") {
//            LaunchedEffect(Unit) {
//                activity.showSystemBars()
//                activity.setStatusBarColor(color)
//            }
//            FirstScreen(navController)
//        }
//        composable("second") {
//            LaunchedEffect(Unit) {
//                activity.showSystemBars()
//                activity.setStatusBarColor(color)
//            }
//            SecondScreen(navController)
//        }
//        composable("fullscreen") {
//            LaunchedEffect(Unit) {
//                activity.hideSystemBars()
//                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            }
//            FullScreenPlayer()
//        }
//    }
//}


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerUi() {
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val player = remember {
        val trackSelector = DefaultTrackSelector(
            context, AdaptiveTrackSelection.Factory()
        )
        ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build().apply {
                val defaultRequestProperties = mapOf<String, String>()
                val httpDataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
                    .setUserAgent("")
                    .setKeepPostFor302Redirects(true)
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                    .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                    .setDefaultRequestProperties(defaultRequestProperties)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context, httpDataSourceFactory)
                val mediaItem = MediaItem.Builder()
                    .setUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                    .build()
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                Lifecycle.Event.ON_RESUME -> player.play()
                Lifecycle.Event.ON_DESTROY -> player.release()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { c ->
                PlayerView(c).apply {
                    this.player = player
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    this.keepScreenOn = true
                }
            }
        )
    }
}
