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
        }
        if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
            // using the incoming number, add to db
            isPhoneCalling = true;
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended, need detect flag
            // from CALL_STATE_OFFHOOK

            if (isPhoneCalling) {

                Handler handler = new Handler();

                //Put in delay because call log is not updated immediately when state changed
                // The dialler takes a little bit of time to write to it 500ms seems to be enough
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // get start of cursor
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

                        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

                        if (cursor.moveToFirst()) {
                            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        }

                        // db stuff

                        Cursor curs = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{contactId}, null, null, null, null);
                        ContentValues values = new ContentValues();

                        if (curs.getCount() != 0) {
                            //update field
//
                            // set the format to sql date time
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(date));

                            db.updateWithOnConflict(UkEntryContract.UkEntry.TABLE_NAME, values, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{contactId}, db.CONFLICT_REPLACE);
                        }

                        cur.close();

                    }


                },500);



                isPhoneCalling = false;
            }

        }
    }

}
