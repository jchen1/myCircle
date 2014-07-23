package f.Ties.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jeff on 7/18/14.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Service
            Intent serviceIntent = new Intent(context, UkService.class);
            context.startService(serviceIntent);
        }
    }
}
