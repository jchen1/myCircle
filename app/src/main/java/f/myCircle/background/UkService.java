package f.myCircle.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import f.myCircle.models.DatabaseManager;
import f.myCircle.models.ContactModel;

/**
 * Created by jeff on 7/18/14.
 */
public class UkService extends Service {
    private ScheduledExecutorService scheduler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyMgr.listen(new TeleListener(this),
                PhoneStateListener.LISTEN_CALL_STATE);
        SMSListener smsListener = new SMSListener(this);
        final DatabaseManager db = new DatabaseManager(this);

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                for (ContactModel contact : db.getAddedContacts()) {
                    if ((new Date()).getTime() > contact.getLastContacted().getTime()+contact.getTtk().getTime()){
                        db.deleteContact(contact);
                    }
                }
            }
        }, 0, 1, TimeUnit.DAYS);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}