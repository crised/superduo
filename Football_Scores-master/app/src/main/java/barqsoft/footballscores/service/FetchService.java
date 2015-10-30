package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FetchService extends IntentService {
    public static final String LOG_TAG = "FetchService";

    public FetchService() {
        super("FetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData(getString(R.string.fetch_service_n2));
        getData(getString(R.string.fetch_service_p2));
        return;
    }

    private void getData(String timeFrame) {
        //Creating fetch URL
        final String BASE_URL = getString(R.string.fetch_service_base_url);
        final String QUERY_TIME_FRAME = getString(R.string.fetch_service_query_time_frame);
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod(getString(R.string.fetch_service_request_method));
            m_connection.addRequestProperty(getString(R.string.fetch_service_request_property),
                    getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception here" + e.getMessage());
        } finally {
            if (m_connection != null) {
                m_connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
        try {
            if (JSON_data != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(JSON_data, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void processJSONdata(String JSONdata, Context mContext, boolean isReal) {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes

        //Match data
        String League;
        String mDate;
        String mTime;
        String Home;
        String Away;
        String Home_goals;
        String Away_goals;
        String match_id;
        String match_day;


        try {
            JSONArray matches = new JSONObject(JSONdata).
                    getJSONArray(getString(R.string.fetch_service_fixtures));


            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<ContentValues>(matches.length());
            for (int i = 0; i < matches.length(); i++) {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(getString(R.string.fetch_service_links))
                        .getJSONObject(getString(R.string.fetch_service_soccer_season)).
                                getString("href");
                League = League.replace(getString(R.string.fetch_service_season_link), "");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (League.equals(getString(R.string.premierleague)) ||
                        League.equals(getString(R.string.fetch_service_serie_a)) ||
                        League.equals(getString(R.string.fetch_service_bundesliga_1)) ||
                        League.equals(getString(R.string.fetch_service_bundesliga_2)) ||
                        League.equals(getString(R.string.fetch_service_primera_division))) {
                    match_id = match_data.getJSONObject(getString(R.string.fetch_service_links))
                            .getJSONObject(getString(R.string.fetch_service_self))
                            .getString("href");
                    match_id = match_id.replace(getString(R.string.fetch_service_match_link), "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id = match_id + Integer.toString(i);
                    }

                    mDate = match_data.getString(getString(R.string.fetch_service_match_date));
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0, mDate.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(mDate + mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0, mDate.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis()
                                    + ((i - 2) * 86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate = mformat.format(fragmentdate);
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG, e.getMessage());
                    }
                    Home = match_data.getString(getString(R.string.fetch_service_home_team));
                    Away = match_data.getString(getString(R.string.fetch_service_away_team));
                    Home_goals = match_data.getJSONObject(getString(R.string.fetch_service_result))
                            .getString(getString(R.string.fetch_service_home_goals));
                    Away_goals = match_data.getJSONObject(getString(R.string.fetch_service_result))
                            .getString(getString(R.string.fetch_service_away_goals));
                    match_day = match_data.getString(getString(R.string.fetch_service_match_day));
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.ScoresTable.MATCH_ID, match_id);
                    match_values.put(DatabaseContract.ScoresTable.DATE_COL, mDate);
                    match_values.put(DatabaseContract.ScoresTable.TIME_COL, mTime);
                    match_values.put(DatabaseContract.ScoresTable.HOME_COL, Home);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_COL, Away);
                    match_values.put(DatabaseContract.ScoresTable.HOME_GOALS_COL, Home_goals);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL, Away_goals);
                    match_values.put(DatabaseContract.ScoresTable.LEAGUE_COL, League);
                    match_values.put(DatabaseContract.ScoresTable.MATCH_DAY, match_day);


                    values.add(match_values);
                }
            }
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }
}

