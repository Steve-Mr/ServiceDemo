package com.example.servicedemo

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.Row
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.servicedemo.Widget.Companion.WATER_WIDGET_PREFS_KEY

    @Composable
    fun WidgetCounter(
        context: Context,
        glassesOfWater:Int,
        modifier: GlanceModifier
    ){
        Text(
            text = context.getString(
                R.string.glasses_of_water_format,
                glassesOfWater
            ),
            modifier = modifier,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(
                    color = Color.White
                )
            )
        )
    }

    @Composable
    fun WidgetGoal(
        context: Context,
        glassesOfWater: Int,
        modifier: GlanceModifier
    ){
        Text(
            text =
                when{
                    glassesOfWater >= 8 -> context.getString(
                        R.string.goal_met
                    )
                    else -> context.getString(
                        R.string.water_goal,
                        8
                    )
                },
            modifier = modifier,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(
                    Color.White
                )
            )
        )
    }

    @Composable
    fun WidgetButtonLayout(
        modifier: GlanceModifier
    ){
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                provider = ImageProvider(
                    resId = R.drawable.ic_baseline_delete_24
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .clickable(
                        onClick = actionRunCallback<ClearWaterClickAction>()
                    )
                    .defaultWeight()
            )
            Image(
                provider = ImageProvider(
                    resId = R.drawable.ic_baseline_add_circle_24
                ),
                contentDescription = null,
                modifier = GlanceModifier
                    .clickable(
                        actionRunCallback<AddWaterClickAction>()
                    )
                    .defaultWeight()
            )
        }
    }

    @Composable
    fun WidgetContent(
        modifier: GlanceModifier
    ){
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val context = LocalContext.current
            val prefs = currentState<Preferences>()
            val glassesOfWater = prefs[intPreferencesKey(WATER_WIDGET_PREFS_KEY)] ?: 0

            WidgetCounter(
                context = context,
                glassesOfWater = glassesOfWater,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            )

            WidgetGoal(
                context = context,
                glassesOfWater = glassesOfWater,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
            )

            WidgetButtonLayout(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .defaultWeight()
            )
        }
    }
