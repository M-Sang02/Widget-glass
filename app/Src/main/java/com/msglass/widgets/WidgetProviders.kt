package com.ms.glasswidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.widget.RemoteViews
import java.util.Locale

private const val MEDIA_PLAY = "com.ms.glasswidgets.MEDIA_PLAY"
private const val MEDIA_NEXT = "com.ms.glasswidgets.MEDIA_NEXT"
private const val MEDIA_PREV = "com.ms.glasswidgets.MEDIA_PREV"
private const val MEDIA_REFRESH = "com.ms.glasswidgets.MEDIA_REFRESH"
private const val VOL_UP = "com.ms.glasswidgets.VOL_UP"
private const val VOL_DOWN = "com.ms.glasswidgets.VOL_DOWN"
private const val VOL_MUTE = "com.ms.glasswidgets.VOL_MUTE"

class MediaStageWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) { MediaWidgetUpdater.handle(context, intent, javaClass); super.onReceive(context, intent) }
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { MediaWidgetUpdater.update(context, manager, it, javaClass) } }
}
class MediaBookWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) { MediaWidgetUpdater.handle(context, intent, javaClass); super.onReceive(context, intent) }
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { MediaWidgetUpdater.update(context, manager, it, javaClass) } }
}
class MediaWaveWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) { MediaWidgetUpdater.handle(context, intent, javaClass); super.onReceive(context, intent) }
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { MediaWidgetUpdater.update(context, manager, it, javaClass) } }
}

/** Shared renderer for the three independent music-widget layouts. */
object MediaWidgetUpdater {
    private val widgetClasses = listOf(MediaStageWidget::class.java, MediaBookWidget::class.java, MediaWaveWidget::class.java)

    fun handle(context: Context, intent: Intent, receiver: Class<*>) {
        when (intent.action) {
            MEDIA_PLAY, MEDIA_NEXT, MEDIA_PREV -> {
                val controller = MediaNotificationListener.activeController(context)
                try {
                    when (intent.action) {
                        MEDIA_PLAY -> if (controller?.playbackState?.state == PlaybackState.STATE_PLAYING) controller.transportControls.pause() else controller?.transportControls?.play()
                        MEDIA_NEXT -> controller?.transportControls?.skipToNext()
                        MEDIA_PREV -> controller?.transportControls?.skipToPrevious()
                    }
                } catch (_: SecurityException) { }
                updateAll(context)
            }
            MEDIA_REFRESH -> updateAll(context)
        }
    }

    fun updateAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        widgetClasses.forEach { cls -> manager.getAppWidgetIds(ComponentName(context, cls)).forEach { update(context, manager, it, cls) } }
    }

    fun update(context: Context, manager: AppWidgetManager, id: Int, cls: Class<*>) {
        val layout = when (cls) {
            MediaBookWidget::class.java -> R.layout.widget_media_book
            MediaWaveWidget::class.java -> R.layout.widget_media_wave
            else -> R.layout.widget_media
        }
        val views = RemoteViews(context.packageName, layout)
        val controller = MediaNotificationListener.activeController(context)
        val metadata = controller?.metadata
        val state = controller?.playbackState
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?.takeIf { it.isNotBlank() } ?: "Chưa phát nhạc"
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)?.takeIf { it.isNotBlank() }
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)?.takeIf { it.isNotBlank() }
            ?: "Mở ứng dụng nhạc tương thích"
        views.setTextViewText(R.id.media_title, title)
        views.setTextViewText(R.id.media_artist, artist)
        views.setTextViewText(R.id.media_play, if (state?.state == PlaybackState.STATE_PLAYING) "Ⅱ" else "▶")
        val position = state?.position ?: 0L
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val progress = if (duration > 0L) ((position * 1000L) / duration).toInt().coerceIn(0, 1000) else 0
        views.setProgressBar(R.id.media_progress, 1000, progress, false)
        views.setTextViewText(R.id.media_elapsed, formatTime(position))
        views.setTextViewText(R.id.media_duration, formatTime(duration))
        val artwork = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART) ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
        if (artwork != null) views.setImageViewBitmap(R.id.media_art, artwork) else views.setImageViewResource(R.id.media_art, R.drawable.ic_album_placeholder)
        views.setOnClickPendingIntent(R.id.media_prev, pending(context, cls, MEDIA_PREV, id * 10 + 1))
        views.setOnClickPendingIntent(R.id.media_play, pending(context, cls, MEDIA_PLAY, id * 10 + 2))
        views.setOnClickPendingIntent(R.id.media_next, pending(context, cls, MEDIA_NEXT, id * 10 + 3))
        manager.updateAppWidget(id, views)
    }

    private fun pending(context: Context, cls: Class<*>, action: String, requestCode: Int) = PendingIntent.getBroadcast(
        context, requestCode, Intent(context, cls).setAction(action), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    private fun formatTime(ms: Long): String {
        val seconds = (ms.coerceAtLeast(0L) / 1000L).toInt()
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60)
    }
}

class VolumeWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (intent.action) {
            VOL_UP -> audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
            VOL_DOWN -> audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
            VOL_MUTE -> audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_TOGGLE_MUTE, 0)
        }
        super.onReceive(context, intent)
        updateAll(context)
    }
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { update(context, manager, it) } }
    private fun updateAll(context: Context) { val m = AppWidgetManager.getInstance(context); m.getAppWidgetIds(ComponentName(context, VolumeWidget::class.java)).forEach { update(context, m, it) } }
    private fun update(context: Context, manager: AppWidgetManager, id: Int) {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val current = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        val views = RemoteViews(context.packageName, R.layout.widget_volume)
        views.setTextViewText(R.id.volume_percent, "Âm lượng ${current * 100 / max}%")
        views.setProgressBar(R.id.volume_progress, 100, current * 100 / max, false)
        views.setOnClickPendingIntent(R.id.volume_up, action(context, VOL_UP, id * 10 + 1))
        views.setOnClickPendingIntent(R.id.volume_down, action(context, VOL_DOWN, id * 10 + 2))
        views.setOnClickPendingIntent(R.id.volume_mute, action(context, VOL_MUTE, id * 10 + 3))
        manager.updateAppWidget(id, views)
    }
    private fun action(c: Context, action: String, code: Int): PendingIntent = PendingIntent.getBroadcast(c, code, Intent(c, VolumeWidget::class.java).setAction(action), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}
