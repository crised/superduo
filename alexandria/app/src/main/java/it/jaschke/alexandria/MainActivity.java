package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;

import static it.jaschke.alexandria.R.integer.duration;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    public static final int ACTIVITY_NORMAL_STATUS = 1;
    public static final int ACTIVITY_BAR_CODE_STATUS = 2;
    public static final int ACTIVITY_NO_NET_STATUS = 3;

    public static final int FRAGMENT_LIST_OF_BOOKS = 0;
    public static final int FRAGMENT_ADD_BOOKS = 1;
    public static final int FRAGMENT_ABOUT = 2;

    private static final int NUMBER_OF_FRAGMENTS = 2;

    public static boolean sIsTablet = false; //may not be static.

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private BroadcastReceiver mMessageReciever;
    private String mBarCodeEsn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIfComingFromBarCodeActivity();
        sIsTablet = isTablet();
        if (sIsTablet) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        mMessageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(getString(R.string.message_event));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReciever, filter);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        String tag;

        if (Utility.getBarCodeAppStatus(this) == ACTIVITY_BAR_CODE_STATUS) {
            AddBook next = new AddBook();
            tag = getResources().getString(R.string.add_book_fragment_tag);
            Bundle nB = new Bundle();
            nB.putString(getResources().getString(R.string.scanned_esn_bundle_key), mBarCodeEsn);
            next.setArguments(nB);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, next, tag)
                    .addToBackStack((String) mTitle)
                    .commit();
            return;
        }

        switch (position) {
            default:
                nextFragment = new ListOfBooks();
                tag = getResources().getString(R.string.list_book_fragment_tag);
                break;
            case FRAGMENT_ADD_BOOKS:
                nextFragment = new AddBook();
                tag = getResources().getString(R.string.add_book_fragment_tag);
                break;
            case FRAGMENT_ABOUT:
                nextFragment = new About();
                tag = getResources().getString(R.string.about_fragment_tag);
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment, tag)
                .addToBackStack((String) mTitle)
                .commit();
    }

    public void setTitle(int titleId) {
        mTitle = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if (findViewById(R.id.right_container) != null) {
            id = R.id.right_container;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack(getString(R.string.book_detail))
                .commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(getString(R.string.message_key)) != null) {
                Toast.makeText(MainActivity.this,
                        intent.getStringExtra(getString(R.string.message_key)),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view) {
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < NUMBER_OF_FRAGMENTS) {
            finish();
        }
        super.onBackPressed();
    }

    private void checkIfComingFromBarCodeActivity() {
        Bundle b = getIntent().getExtras();

        if (b != null) {
            String esn = b.
                    getString(getResources().
                            getString(R.string.scanned_esn_bundle_key), "");
            if (esn != null && !esn.isEmpty()) {
                mBarCodeEsn = esn;
                getIntent().removeExtra(getResources().
                        getString(R.string.scanned_esn_bundle_key));
                Utility.setBarCodeAppStatus(this);
            }
            return;
        }
    }
}
