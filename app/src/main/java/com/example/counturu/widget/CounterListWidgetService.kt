package com.example.counturu.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.counturu.R
import com.example.counturu.data.Counter
import com.example.counturu.data.CounterDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class CounterListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CounterListRemoteViewsFactory(applicationContext)
    }
}

class CounterListRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var counters: List<Counter> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        runBlocking {
            val database = CounterDatabase.getDatabase(context)
            val allCounters = database.counterDao().getAllCounters().first()
            counters = allCounters
                .filter { it.targetDateTime > System.currentTimeMillis() }
                .sortedBy { it.targetDateTime }
                .take(5) // Show max 5 items
        }
    }

    override fun onDestroy() {
        counters = emptyList()
    }

    override fun getCount(): Int = counters.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)

        if (position < counters.size) {
            val counter = counters[position]
            val timeRemaining = counter.targetDateTime - System.currentTimeMillis()
            val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)
            val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24

            views.setTextViewText(R.id.widget_list_item_icon, counter.icon)
            views.setTextViewText(R.id.widget_list_item_title, counter.title)
            views.setTextViewText(R.id.widget_list_item_days, days.toString())

            val timeText = when {
                days > 0 -> "$days days, $hours hrs left"
                hours > 0 -> "$hours hours left"
                else -> "Very soon!"
            }
            views.setTextViewText(R.id.widget_list_item_time, timeText)

            // Fill in intent for click
            val fillInIntent = Intent()
            views.setOnClickFillInIntent(R.id.widget_list_item_container, fillInIntent)
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}

