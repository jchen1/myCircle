package f.myCircle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by jeff on 7/18/14.
 */
public class DatabaseManager {

    SQLiteDatabase db;
    Context ctx;

    public DatabaseManager(SQLiteDatabase _db, Context ctx) {
        db = _db;
        this.ctx = ctx;
    }

    public boolean deleteContact(String phone) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);

        try {
            cur.moveToFirst();
            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            ctx.getContentResolver().delete(uri, null, null);
            if (deleteContactUkdb(lookupKey) == 0) {
                return false;
            }
            return true;

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }

    private int deleteContactUkdb(String lookupKey) {
        return db.delete(UkEntryContract.UkEntry.TABLE_NAME, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{lookupKey});
    }
}
