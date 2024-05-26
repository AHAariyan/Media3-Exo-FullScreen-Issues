package com.aariyan.media3_exoplayer_fullscreen_issues

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController

@OptIn(UnstableApi::class)
@Composable
internal fun SecondScreen(navController: NavHostController, activity: Activity, color: Int) {
    LaunchedEffect(Unit) {
        activity.showSystemBars()
        activity.setStatusBarColor(color)
    }

    val context = LocalContext.current

    // Initialize the ExoPlayer
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
    val lifecycleOwner = LocalLifecycleOwner.current
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

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.systemBars.asPaddingValues())) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { c ->
                PlayerView(c).apply {
                    this.player = player
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    this.keepScreenOn = true
                }
            }
        )
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { navController.navigate("fullscreen") }
        ) {
            Text("Go Fullscreen")
        }
    }
}
