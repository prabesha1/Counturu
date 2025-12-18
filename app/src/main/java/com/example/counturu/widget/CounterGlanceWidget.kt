package com.example.counturu.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.counturu.MainActivity
import com.example.counturu.R
import com.example.counturu.data.Counter
import com.example.counturu.data.CounterDatabase
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class CounterGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = CounterDatabase.getDatabase(context)
        val counters = database.counterDao().getAllCounters().first()

        val nextCounter = counters
            .filter { it.targetDateTime > System.currentTimeMillis() }
            .minByOrNull { it.targetDateTime }

        provideContent {
            GlanceTheme {
                WidgetContent(counter = nextCounter)
            }
        }
    }

    @Composable
    private fun WidgetContent(counter: Counter?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(24.dp)
                .background(ColorProvider(Color(0xFF1C1C1E)))
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (counter != null) {
                val timeRemaining = counter.targetDateTime - System.currentTimeMillis()
                val days = TimeUnit.MILLISECONDS.toDays(timeRemaining)
                val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60

                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Text(
                        text = counter.icon,
                        style = TextStyle(fontSize = 32.sp)
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    // Title
                    Text(
                        text = counter.title,
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )

                    Spacer(modifier = GlanceModifier.height(12.dp))

                    // Countdown
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CountdownItem(value = days.toString(), label = "D")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        CountdownItem(value = hours.toString(), label = "H")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        CountdownItem(value = minutes.toString(), label = "M")
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        CountdownItem(value = seconds.toString(), label = "S")
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⏱️",
                        style = TextStyle(fontSize = 40.sp)
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Text(
                        text = "No upcoming events",
                        style = TextStyle(
                            color = ColorProvider(Color.White.copy(alpha = 0.7f)),
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Tap to add",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF0A84FF)),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun CountdownItem(value: String, label: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = GlanceModifier
                    .size(44.dp)
                    .cornerRadius(12.dp)
                    .background(ColorProvider(Color(0xFF2C2C2E))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.padStart(2, '0'),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                    fontSize = 10.sp
                )
            )
        }
    }
}

class CounterGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CounterGlanceWidget()
}

