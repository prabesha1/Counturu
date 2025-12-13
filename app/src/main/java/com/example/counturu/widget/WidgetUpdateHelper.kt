package com.example.counturu.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

object WidgetUpdateHelper {

    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        // Update large widgets
        val largeWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, CounterWidgetProvider::class.java)
        )
        for (widgetId in largeWidgetIds) {
            CounterWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId)
        }

        // Update small widgets
        val smallWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, CounterWidgetSmallProvider::class.java)
        )
        for (widgetId in smallWidgetIds) {
            CounterWidgetSmallProvider.updateAppWidget(context, appWidgetManager, widgetId)
        }
    }
}

