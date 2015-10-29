package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * Created by crised on 29-10-15.
 */
public class TodayWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_today_small);

            views.setTextViewText(R.id.widget_score_1, "1-0");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }
}
