package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by crised on 30-10-15.
 */
public class TodayWidgetIntentService extends IntentService {

    public static final String ACTION_DATA_UPDATED =
            "barqsoft.footballscores.ACTION_DATA_UPDATED";

    private static final String LOG_TAG = "TodayService";
    private static final String EMPTY_CELL = "-1";

    private static final String[] PROJECTION_COLUMNS = {
            DatabaseContract.ScoresTable.DATE_COL,
            DatabaseContract.ScoresTable.HOME_COL,
            DatabaseContract.ScoresTable.AWAY_COL,
            DatabaseContract.ScoresTable.HOME_GOALS_COL,
            DatabaseContract.ScoresTable.AWAY_GOALS_COL,

    };

    private static final int INDEX_HOME = 1;
    private static final int INDEX_AWAY = 2;
    private static final int INDEX_HOME_GOALS = 3;
    private static final int INDEX_AWAY_GOALS = 4;

    private String[] dateString = new String[1];
    private String scoreContent = "Sorry no data";


    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));
        Context context = getApplicationContext();

        //Only today.
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat(
                getString(R.string.pager_fragment_date_format_1));
        dateString[0] = mformat.format(fragmentdate);

        Uri uri = DatabaseContract.ScoresTable.buildScoreWithDate();
        Cursor cursor = getContentResolver().query(uri, PROJECTION_COLUMNS, null, dateString,
                DatabaseContract.ScoresTable.DATE_COL + " DESC");


        if (cursor != null) {
            if (cursor.moveToFirst())
                buildString(cursor);
            cursor.close();
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_today_small);

            views.setTextViewText(R.id.widget_score, scoreContent);
            views.setImageViewResource(R.id.widget_icon, R.drawable.ic_launcher);

            //Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            //Update Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        Log.d("TodayWidgetUpdate", "service updated");


    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    private void buildString(Cursor c) {

        StringBuilder sb = new StringBuilder();

        String home = c.getString(INDEX_HOME);
        String home_goals = c.getString(INDEX_HOME_GOALS);
        String away = c.getString(INDEX_AWAY);
        String away_goals = c.getString(INDEX_AWAY_GOALS);

        if (home.equals(EMPTY_CELL)) return;
        sb.append(home + " ");
        //goals might not be ready.
        if (!home_goals.equals(EMPTY_CELL)) sb.append(home_goals);
        sb.append(" - ");
        if (!away_goals.equals(EMPTY_CELL)) sb.append(away_goals + "  ");
        sb.append(away);
        scoreContent = sb.toString();

    }

    public static class AlarmReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("AlarmReceiver", "Alarm Ready");
            Log.e("AlarmReceiver", "Alarm Ready");


            Intent broadcastIntent = new Intent(ACTION_DATA_UPDATED)
                    .setPackage(context.getPackageName());

            context.sendBroadcast(broadcastIntent);


        }
    }
}
