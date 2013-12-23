package se.slide.skypewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class SkypeAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        for (int appWidgetId: appWidgetIds) {
            String username = ConfigurationActivity.loadTitlePref(context, appWidgetId);
            
            updateAppWidget(context, appWidgetManager, appWidgetId, username);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String username) {
        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.username, username);
        
        Intent sky = new Intent("android.intent.action.VIEW");
        sky.setData(Uri.parse("skype:" + username + "?call&video=true"));
        
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, sky, 0);
        
        views.setOnClickPendingIntent(R.id.layout, pendingIntent);
        
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
