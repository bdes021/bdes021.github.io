package com.example.bruno_desousa_trakker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrakkerWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.trakker_widget_layout);
        
        SharedPreferences prefs = context.getSharedPreferences("TrakkerPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("current_user", null);

        if (username != null) {
            TrakkerRepository repo = new TrakkerRepository(context);
            List<MyEvent> events = repo.getEvents(username);
            
            StringBuilder sb = new StringBuilder();
            int count = 0;
            Calendar now = Calendar.getInstance();
            
            for (MyEvent event : events) {
                if (event.getDate().after(now) && count < 3) {
                    Calendar date = event.getDate();
                    String dateStr = String.format(Locale.getDefault(), "%02d/%02d",
                            date.get(Calendar.MONTH) + 1,
                            date.get(Calendar.DAY_OF_MONTH));
                    String timeStr = String.format(Locale.getDefault(), "%02d:%02d %s",
                            date.get(Calendar.HOUR) == 0 ? 12 : date.get(Calendar.HOUR),
                            date.get(Calendar.MINUTE),
                            date.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                    
                    sb.append(event.getTitle())
                      .append("\n")
                      .append(dateStr).append(" @ ").append(timeStr)
                      .append("\n\n");
                    count++;
                }
            }
            
            if (count == 0) {
                views.setTextViewText(R.id.widget_event_list, "No upcoming events");
            } else {
                views.setTextViewText(R.id.widget_event_list, sb.toString());
            }
        } else {
            views.setTextViewText(R.id.widget_event_list, "Login to see events");
        }

        // Launch app on click
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_event_list, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
