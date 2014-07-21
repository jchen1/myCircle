package f.myCircle;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by jeff on 7/19/14.
 */
public class SMSListener {

    private ContentObserver observer;
    private Context context;
    private DatabaseManager db;

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
        context = _context;
        db = new DatabaseManager(_db, context);
        registerContentObserver();
    }

    /**
     * Register an observer for listening outgoing sms events.
     *
     */
    private void registerContentObserver() {
        observer = new ContentObserver(null) {
            public void onChange(boolean selfChange) {
                Cursor cursor = context.getContentResolver().query(Uri.parse(CONTENT_SMS), null, null, null, null);
                if (cursor.moveToNext()) {
                    String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    // Only processing outgoing sms event & only when it
                    // is sent successfully (available in SENT box).
                    if (protocol != null || type != MESSAGE_TYPE_SENT) {
                        return;
                    }
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
        };
        context.getContentResolver().registerContentObserver(
                Uri.parse(CONTENT_SMS), true, observer);
    }
}
