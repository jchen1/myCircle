package f.myCircle;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jeff on 7/19/14.
 */
public class Checker implements Runnable {
    SQLiteDatabase db;
    Context ctx;

    public Checker(SQLiteDatabase _db, Context _ctx) {
        db = _db;
        ctx = _ctx;
    }

    @Override
    public void run() {
        Log.v("yo", "poop");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Cursor cur = db.query(UkEntryContract.UkEntry.TABLE_NAME, null, null, null, null, null, null, null);
        while(cur.moveToNext()) {
            try {
                Date lastContact = sdf.parse(cur.getString(cur.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT)));
                Date timeToKill = sdf.parse(cur.getString(cur.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_TTK)));
                if ((new Date()).getTime() < lastContact.getTime()+timeToKill.getTime()){
                    String eid = cur.getString(cur.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID));
                    String cName = cur.getString(cur.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_FIRSTNAME));
                    db.delete(UkEntryContract.UkEntry.TABLE_NAME, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{eid});

                    Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
                    Cursor curs = ctx.getContentResolver().query(contactUri, null, ContactsContract.PhoneLookup._ID + "=?", new String[]{eid}, null);

                    try {
                        curs.moveToFirst();
                        String lookupKey = curs.getString(curs.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);

                    } catch (Exception e) {
                        System.out.println(e.getStackTrace());
                    }
                    curs.close();

                    Log.v("deleted", cName);

                }
            }catch(Exception e){
            }
        }
        cur.close();
    }
}

