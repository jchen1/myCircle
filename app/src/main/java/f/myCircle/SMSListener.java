package f.myCircle;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeff on 7/19/14.
 */
public class SMSListener {

    private ContentObserver observer;
    private Context context;
    private SQLiteDatabase db;

    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_NEW_OUTGOING_SMS = "android.provider.Telephony.NEW_OUTGOING_SMS";

    private static final String CONTENT_SMS = "content://sms";

    /**
     * Constant from Android SDK
     */
    private static final int MESSAGE_TYPE_SENT = 2;

    private static final String[] INTENTS = {ACTION_SMS_RECEIVED, ACTION_NEW_OUTGOING_SMS};


    public SMSListener(SQLiteDatabase _db, Context _context) {
        super();
        db = _db;
        context = _context;
        registerContentObserver();
    }

    /**
     * Register an observer for listening outgoing sms events.
     *
     */
    private void registerContentObserver() {
        observer = new ContentObserver(null) {
            public void onChange(boolean selfChange) {
                Cursor cursor = context.getContentResolver().query(
                        Uri.parse(CONTENT_SMS), null, null, null, null);
                if (cursor.moveToNext()) {
                    String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    // Only processing outgoing sms event & only when it
                    // is sent successfully (available in SENT box).
                    if (protocol != null || type != MESSAGE_TYPE_SENT) {
                        return;
                    }
                    int dateColumn = cursor.getColumnIndex("date");
                    int bodyColumn = cursor.getColumnIndex("body");
                    int addressColumn = cursor.getColumnIndex("address");

                    String from = "0";
                    String lastCallnumber = cursor.getString(addressColumn);
                    Date now = new Date(cursor.getLong(dateColumn));
                    String message = cursor.getString(bodyColumn);
                    Log.v("SMSListener", "to=" + lastCallnumber);

                    String name = null;
                    String contactId = null;
                    InputStream input = null;

                    // define the columns I want the query to return
                    String[] projection = new String[]{
                            ContactsContract.PhoneLookup.DISPLAY_NAME,
                            ContactsContract.PhoneLookup._ID};

                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(lastCallnumber));
                    Log.v("ukService", "uri=" + uri.toString());

                    Cursor cur = context.getContentResolver().query(uri, projection, null, null, null);

                    if (cur.moveToFirst()) {
                        contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
                        name = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                        Log.v("ukService", contactId);
                        Log.v("ukService", name);

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
                        Cursor asdf = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, null, null, null, null, null, null);

                        asdf.moveToFirst();
                        while (asdf.moveToNext()) {
                            Log.v("DB ENTRY TEST", "to=" + asdf.getString(0));
                        }
                    } else {
                        // insert new
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID, contactId);
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_FIRSTNAME, name.split(" ")[0]);
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTNAME, name.split(" ")[1]);

                        // set the format to sql date time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(date));

                        long newRowId = db.insert(UkEntryContract.UkEntry.TABLE_NAME, "foo", values);
                        cur.close();
                    }

                }
                cursor.close();

                return;
            }
        };
        context.getContentResolver().registerContentObserver(
                Uri.parse(CONTENT_SMS), true, observer);
    }
}
