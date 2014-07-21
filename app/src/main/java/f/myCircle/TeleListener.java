package f.myCircle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by jeff on 7/18/14.
 */
public class TeleListener extends PhoneStateListener {

    private DatabaseManager db;
    private Context context;
    private boolean isPhoneCalling;

    public TeleListener(Context _context) {
        super();
        context = _context;
        db = new DatabaseManager(context);
        isPhoneCalling = false;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            isPhoneCalling = true;
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, need detect flag
            // from CALL_STATE_OFFHOOK

            if (isPhoneCalling) {

                Handler handler = new Handler();

                //Put in delay because call log is not updated immediately when state changed
                // The dialer takes a little bit of time to write to it; 500ms seems to be enough
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER}, null, null, CallLog.Calls.DATE +" desc");
                        if (cursor.moveToNext()) {
                            String lastCallNumber = cursor.getString(cursor.getColumnIndex("address"));
                            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(lastCallNumber));

                            Cursor cur = context.getContentResolver().query(uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);

                            if (cur.moveToFirst()) {
                                ContactModel contact = new ContactModel();
                                contact.setContactId(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID))));
                                db.touchContact(contact);
                            }
                            cur.close();
                        }
                        cursor.close();
                    }

                },500);

                isPhoneCalling = false;
            }

        }
    }

}
