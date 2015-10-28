package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    public static int sSelected_match_id;
    public static int sCurrent_fragment = 2;

    private PagerFragment mPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mPagerFragment = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPagerFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.main_activity_pager_current_key),
                mPagerFragment.mPagerHandler.getCurrentItem());
        outState.putInt(getString(R.string.main_activity_selected_match_key), sSelected_match_id);
        getSupportFragmentManager().putFragment(outState, getString(R.string.main_activity_pager),
                mPagerFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        sCurrent_fragment = savedInstanceState.getInt(
                getString(R.string.main_activity_pager_current_key));
        sSelected_match_id = savedInstanceState.getInt(
                getString(R.string.main_activity_selected_match_key));
        mPagerFragment = (PagerFragment) getSupportFragmentManager()
                .getFragment(savedInstanceState, getString(R.string.main_activity_pager));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
