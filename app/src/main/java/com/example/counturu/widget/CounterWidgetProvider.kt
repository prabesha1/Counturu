package com.example.counturu.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.counturu.R
import com.example.counturu.data.CounterDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CounterWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = CounterDatabase.getDatabase(context)
                val counters = database.counterDao().getAllCounters().first()

                // Get the next upcoming counter
                val nextCounter = counters
                    .filter { it.targetDateTime > System.currentTimeMillis() }
                    .minByOrNull { it.targetDateTime }

                val views = RemoteViews(context.packageName, R.layout.widget_counter)

                if (nextCounter != null) {
                    val timeRemaining = nextCounter.targetDateTime - System.currentTimeMillis()
                    val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)
                    val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60

                    views.setTextViewText(R.id.widget_icon, nextCounter.icon)
                    views.setTextViewText(R.id.widget_title, nextCounter.title)
                    views.setTextViewText(R.id.widget_days, days.toString().padStart(2, '0'))
                    views.setTextViewText(R.id.widget_hours, hours.toString().padStart(2, '0'))
                    views.setTextViewText(R.id.widget_minutes, minutes.toString().padStart(2, '0'))
                    views.setTextViewText(R.id.widget_seconds, seconds.toString().padStart(2, '0'))
                } else {
                    views.setTextViewText(R.id.widget_icon, "⏱️")
                    views.setTextViewText(R.id.widget_title, "No upcoming events")
                    views.setTextViewText(R.id.widget_days, "--")
                    views.setTextViewText(R.id.widget_hours, "--")
                    views.setTextViewText(R.id.widget_minutes, "--")
                    views.setTextViewText(R.id.widget_seconds, "--")
                }

                // Create intent to open app
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                val pendingIntent = android.app.PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

