package barqsoft.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by crised on 30-10-15.
 */
public class FootballSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static FootballSyncAdapter sFootballSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sFootballSyncAdapter == null) {
                sFootballSyncAdapter = new FootballSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return sFootballSyncAdapter.getSyncAdapterBinder();
    }
}
