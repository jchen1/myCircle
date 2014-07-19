package f.myCircle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeff on 7/18/14.
 */
public class TeleListener extends PhoneStateListener {

    private SQLiteDatabase db;
    private Context context;
    private boolean isPhoneCalling;

    public TeleListener(SQLiteDatabase _db, Context _context) {
        super();
        db = _db;
        context = _context;
        isPhoneCalling = false;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
            Log.i("ukTeleListener", "RINGING, number: " + incomingNumber);
        }
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            // using the incoming number, add to db
            Log.i("ukTeleListener", "OFFHOOK");
            isPhoneCalling = true;

        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, need detect flag
            // from CALL_STATE_OFFHOOK
            Log.i("ukTeleListener", "IDLE number");

            if (isPhoneCalling) {

                Handler handler = new Handler();

                //Put in delay because call log is not updated immediately when state changed
                // The dialler takes a little bit of time to write to it 500ms seems to be enough
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // get start of cursor
                        Log.i("CallLogDetailsActivity", "Getting Log activity...");
                        String[] projection = new String[]{CallLog.Calls.NUMBER};
                        Cursor cur = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, CallLog.Calls.DATE +" desc");
                        cur.moveToFirst();
                        String lastCallnumber = cur.getString(0);


                        String name = null;
                        String contactId = null;
                        InputStream input = null;

                        // define the columns I want the query to return
                        projection = new String[]{
                                ContactsContract.PhoneLookup.DISPLAY_NAME,
                                ContactsContract.PhoneLookup._ID};

                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(lastCallnumber));
                        Log.v("ukService", "uri=" + uri.toString());

                        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

                        if (cursor.moveToFirst()) {
                            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                            Log.v("ukService", contactId);
                            Log.v("ukService", name);

                        }

                        // db stuff

                        ContentValues values = new ContentValues();
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID, contactId);
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_FIRSTNAME, name.split(" ")[0]);
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTNAME, name.split(" ")[1]);

                        // set the format to sql date time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(date));

                        long newRowId = db.insert(UkEntryContract.UkEntry.TABLE_NAME, "foo", values);

                        cursor.close();
                        cur.close();

                    }


                },500);



                isPhoneCalling = false;
            }

        }
    }

}
