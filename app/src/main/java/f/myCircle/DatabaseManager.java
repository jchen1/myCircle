package f.myCircle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public List<ContactModel> getAllContacts() {
        List<ContactModel> contacts = new ArrayList<ContactModel>();
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        Cursor cursor = ctx.getContentResolver().query(uri, projection, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, null);

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            ContactModel item = new ContactModel(name, Integer.parseInt(contactId));
            Cursor selectedCursor = db.query(UkEntryContract.UkEntry.TABLE_NAME,
                                            new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID},
                                            UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=" + contactId,
                                            null, null, null, null);
            item.setSelected(selectedCursor.getCount() > 0);
            selectedCursor.close();
            contacts.add(item);
        }
        cursor.close();

        return contacts;
    }

    public List<ContactModel> getAddedContacts() {
        Cursor cursor = db.query(UkEntryContract.UkEntry.TABLE_NAME, null, null, null, null, null, null, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        List<ContactModel> addedContacts = new ArrayList<ContactModel>();
        while (cursor.moveToNext()) {
            ContactModel contact = new ContactModel();
            contact.setName(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_NAME)));
            contact.setUkId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry._ID))));
            contact.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID))));
            try {
                contact.setLastContacted(sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT))));
                contact.setTtk(sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_TTK))));
            } catch (Exception e) {}
            addedContacts.add(contact);
        }

        cursor.close();

        return addedContacts;
    }

    /* for each ContactModel, adds the contact to our db iff they do not exist */
    public long addContact(ContactModel contact) {
        Log.v("DatabaseManager", "addContact touchContact id="+contact.getContactId());

        long ret = 0;
        Cursor curs = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{"" + contact.getContactId()}, null, null, null, null);
        ContentValues values = new ContentValues();
        if (curs.getCount() == 0) {

            values.put(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID, contact.getContactId());
            values.put(UkEntryContract.UkEntry.COLUMN_NAME_NAME, contact.getName());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(new Date()));
            long oneMonthMillis = 1000l * 60l * 60l * 24l * 30l;
            Date ttkDate = new Date(oneMonthMillis);
            values.put(UkEntryContract.UkEntry.COLUMN_NAME_TTK, dateFormat.format(ttkDate));

            ret = db.insert(UkEntryContract.UkEntry.TABLE_NAME, "don't worry about this. no seriously.", values);
        }
        curs.close();
        return ret;
    }

    public boolean touchContact(ContactModel contact) {
        Log.v("DatabaseManager", "touchContact id="+contact.getContactId());
        long ret = 0;
        Cursor curs = db.query(UkEntryContract.UkEntry.TABLE_NAME, new String[]{UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID}, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()}, null, null, null, null);
        ContentValues values = new ContentValues();

        if (curs.getCount() != 0) {
            Log.v("DatabaseManager", "touchContact id="+contact.getContactId());

            // set the format to sql date time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values.put(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(new Date()));

            ret = db.updateWithOnConflict(UkEntryContract.UkEntry.TABLE_NAME, values, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()}, db.CONFLICT_REPLACE);
        }
        curs.close();
        Log.v("DatabaseManager", "changed="+ret);

        return (ret > 0);
    }

    /* removeContact() removes from our db; deleteContact() deletes entirely. The ContactModel should have its Android database id.  */
    public int removeContact(ContactModel contact) {
        return db.delete(UkEntryContract.UkEntry.TABLE_NAME, UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()});
    }

    public int deleteContact(ContactModel contact) {
        removeContact(contact);

        int ret = 0;

        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(contactUri, null, ContactsContract.PhoneLookup._ID + "=?", new String[]{""+contact.getContactId()}, null);

        try {
            cursor.moveToFirst();
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            ret = ctx.getContentResolver().delete(uri, null, null);

        } catch (Exception e) {}
        cursor.close();
        return ret;
    }
}
