package com.ms.glasswidgets

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class MediaNotificationListener : NotificationListenerService() {
    private val callbacks = mutableMapOf<MediaController, MediaController.Callback>()
    private var sessionManager: MediaSessionManager? = null
    private val sessionsChanged = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        registerControllers(controllers.orEmpty())
        MediaWidgetUpdater.updateAll(this)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        try {
            sessionManager?.addOnActiveSessionsChangedListener(sessionsChanged, ComponentName(this, MediaNotificationListener::class.java))
            registerControllers(sessionManager?.getActiveSessions(ComponentName(this, MediaNotificationListener::class.java)).orEmpty())
        } catch (_: SecurityException) { }
        MediaWidgetUpdater.updateAll(this)
    }

    override fun onListenerDisconnected() {
        try { sessionManager?.removeOnActiveSessionsChangedListener(sessionsChanged) } catch (_: Exception) { }
        callbacks.forEach { (controller, callback) -> try { controller.unregisterCallback(callback) } catch (_: Exception) { } }
        callbacks.clear()
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) { refresh() }
    override fun onNotificationRemoved(sbn: StatusBarNotification) { refresh() }

    private fun refresh() {
        try {
            val controllers = sessionManager?.getActiveSessions(ComponentName(this, MediaNotificationListener::class.java)).orEmpty()
            registerControllers(controllers)
        } catch (_: SecurityException) { }
        MediaWidgetUpdater.updateAll(this)
    }

    private fun registerControllers(controllers: List<MediaController>) {
        val active = controllers.toSet()
        callbacks.keys.filter { it !in active }.forEach { old -> callbacks.remove(old)?.let { try { old.unregisterCallback(it) } catch (_: Exception) { } } }
        controllers.forEach { controller ->
            if (!callbacks.containsKey(controller)) {
                val callback = object : MediaController.Callback() {
                    override fun onMetadataChanged(metadata: android.media.MediaMetadata?) { MediaWidgetUpdater.updateAll(this@MediaNotificationListener) }
                    override fun onPlaybackStateChanged(state: PlaybackState?) { MediaWidgetUpdater.updateAll(this@MediaNotificationListener) }
                    override fun onSessionDestroyed() { MediaWidgetUpdater.updateAll(this@MediaNotificationListener) }
                }
                callbacks[controller] = callback
                try { controller.registerCallback(callback) } catch (_: Exception) { callbacks.remove(controller) }
            }
        }
    }

    companion object {
        fun activeController(context: Context): MediaController? = try {
            val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val sessions = manager.getActiveSessions(ComponentName(context, MediaNotificationListener::class.java))
            sessions.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
                ?: sessions.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PAUSED }
                ?: sessions.firstOrNull()
        } catch (_: Exception) { null }
    }
}
