package ru.nikita.challengetimer.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import ru.nikita.challengetimer.R
import ru.nikita.challengetimer.data.MarathonManager
import java.util.Locale

class TimerWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE = "ru.nikita.challengetimer.ACTION_UPDATE_WIDGET"
        private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id -> updateWidget(context, appWidgetManager, id) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_UPDATE == intent.action) {
            val id = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                updateWidget(context, AppWidgetManager.getInstance(context), id)
            }
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget)

        if (!MarathonManager.hasActive(context)) {
            views.setTextViewText(
                R.id.tvMarathonInfo,
                context.getString(R.string.no_active_challenge)
            )
            views.setTextViewText(R.id.time, "--:--:--")
            views.setViewVisibility(R.id.pbProgress, View.GONE)
        } else {
            val days = MarathonManager.getActiveDays(context)
            val elapsed = System.currentTimeMillis() - MarathonManager.getStartTime(context)

            val day = elapsed / 86400000
            val h = (elapsed / 3600000) % 24
            val m = (elapsed / 60000) % 60
            val s = (elapsed / 1000) % 60

            views.setTextViewText(
                R.id.tvMarathonInfo,
                "${context.getString(R.string.label_marathon)}: $days ${context.getString(R.string.label_days_short)}"
            )

            var text = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)

            if (day >= 1)
                text = "$day дн.$text"

            views.setTextViewText(R.id.time, text)
            views.setViewVisibility(R.id.pbProgress, View.VISIBLE)

            // Расчёт прогресса (0–100%)
            val totalDuration = days * 24L * 60L * 60L * 1000L
            val progress = ((elapsed.toFloat() / totalDuration) * 100).toInt().coerceIn(0, 100)


            views.setProgressBar(R.id.pbProgress, 100, progress, false)
        }

        /// Кнопка обновления
        val updateIntent = Intent(context, TimerWidgetProvider::class.java).apply {
            action = ACTION_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        views.setOnClickPendingIntent(
            R.id.btnUpdate,
            PendingIntent.getBroadcast(context, widgetId, updateIntent, FLAGS)
        )

        /// Клик по виджету → открытие приложения
        val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        views.setOnClickPendingIntent(
            R.id.widget_root,
            PendingIntent.getActivity(context, 0, openIntent, FLAGS)
        )

        appWidgetManager.updateAppWidget(widgetId, views)
    }
}