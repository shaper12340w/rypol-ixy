package dev.shaper.rypolixy.utils.musicplayer

import dev.shaper.rypolixy.utils.musicplayer.MediaUtils.MediaPlatform
import kotlin.time.Duration

sealed class MediaTrack {

    abstract val title      : String
    abstract val duration   : Duration
    abstract val url        : String?
    abstract val source     : MediaPlatform

    data class Track(
        override val title      : String,
        override val duration   : Duration,
        override val url        : String?,
        override val source     : MediaPlatform,
        val id          : String,
        val author      : String,
        val thumbnail   : String?,
        val data        : MediaBehavior
    ) : MediaTrack() {

        fun hyperlink(): String {
            return "[$title ($duration)]($url)"
        }

    }

    data class Playlist(
        override val title      : String,
        override val duration   : Duration,
        override val url        : String?,
        override val source     : MediaPlatform,
        val isSeek: Boolean,
        val tracks: MutableList<MediaTrack>,
    ) : MediaTrack()

    data class FlatTrack(
        override val title      : String,
        override val duration   : Duration,
        override val url        : String?,
        override val source     : MediaPlatform,
    ) :MediaTrack()

}