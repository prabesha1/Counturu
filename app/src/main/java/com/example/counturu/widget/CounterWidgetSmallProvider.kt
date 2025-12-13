package com.example.counturu.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.counturu.R
import com.example.counturu.data.CounterDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CounterWidgetSmallProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
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

                val nextCounter = counters
                    .filter { it.targetDateTime > System.currentTimeMillis() }
                    .minByOrNull { it.targetDateTime }

                val views = RemoteViews(context.packageName, R.layout.widget_counter_small)

                if (nextCounter != null) {
                    val timeRemaining = nextCounter.targetDateTime - System.currentTimeMillis()
                    val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)

                    views.setTextViewText(R.id.widget_small_icon, nextCounter.icon)
                    views.setTextViewText(R.id.widget_small_days, days.toString())
                    views.setTextViewText(R.id.widget_small_label, if (days == 1L) "day" else "days")
                } else {
                    views.setTextViewText(R.id.widget_small_icon, "⏱️")
                    views.setTextViewText(R.id.widget_small_days, "--")
                    views.setTextViewText(R.id.widget_small_label, "days")
                }

                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                val pendingIntent = android.app.PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_small_container, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

