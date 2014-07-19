package f.myCircle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeff on 7/18/14.
 */
public class UkService extends Service {
    private SQLiteDatabase db;
    private ScheduledExecutorService scheduler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.v("ukService", "hello world");

        UkDbHelper helper = new UkDbHelper(this);
        db = helper.getWritableDatabase();
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyMgr.listen(new TeleListener(db, this),
                PhoneStateListener.LISTEN_CALL_STATE);
        SMSListener smsListener = new SMSListener(db, this);
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Checker(db, this), 1, 1, TimeUnit.MINUTES);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}