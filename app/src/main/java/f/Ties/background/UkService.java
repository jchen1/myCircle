package f.Ties.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.f.myCircle.R;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import f.Ties.activities.MainActivity;
import f.Ties.models.DatabaseManager;
import f.Ties.models.ContactModel;

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
                    long time = new Date().getTime();
                    long expire = contact.getLastContacted().getTime() + contact.getTtk().getTime();
                    if (time > expire){
                        db.deleteContact(contact);
                    }
                    else if (time + (1000l * 60l * 60 * 24) > expire) { // one day for testing
                        Notification.Builder mBuilder =
                                new Notification.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle("My notification")
                                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
// Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES);

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}