package com.example.mitsichury.mp3player;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.mitsichury.simplemp3player.R;


/**
 * Implementation of App Widget functionality.
 */
public class widget_class extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_class);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Intent to connect to the main activity
        Intent callIntent = new Intent(context, MainActivity.class);
        // Start un new Pending Intent (retrieve the current info of the app) if needed
        PendingIntent clickPI=PendingIntent.getActivity(context, 0, callIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.bt_play, clickPI);
        views.setOnClickPendingIntent(R.id.bt_pause, clickPI);
        views.setTextViewText(R.id.tv_songName, "test");
        views.setTextViewText(R.id.tv_duration, "1,12");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

