package f.Ties.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import f.Ties.models.ContactModel;


/**
 * Created by jeff on 7/18/14.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    SQLiteDatabase db;
    Context ctx;

    public DatabaseManager(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        db = super.getWritableDatabase();
        this.ctx = ctx;
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Uk.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + ContactModel.ContactEntry.TABLE_NAME + " (" +
                    ContactModel.ContactEntry._ID + " INTEGER PRIMARY KEY," +
                    ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + INT_TYPE + COMMA_SEP +
                    ContactModel.ContactEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactModel.ContactEntry.COLUMN_NAME_LASTCONTACT + DATE_TYPE + COMMA_SEP +
                    ContactModel.ContactEntry.COLUMN_NAME_TTK + DATE_TYPE + COMMA_SEP +
                    ContactModel.ContactEntry.COLUMN_NAME_CONTACTHISTORY + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ContactModel.ContactEntry.TABLE_NAME;

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
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
            Cursor selectedCursor = db.query(ContactModel.ContactEntry.TABLE_NAME,
                                            new String[]{ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID},
                                            ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + "=" + contactId,
                                            null, null, null, null);
            item.setSelected(selectedCursor.getCount() > 0);
            selectedCursor.close();
            contacts.add(item);
        }
        cursor.close();

        return contacts;
    }

    public void close() {
        db.close();
    }

    public List<ContactModel> getAddedContacts() {
        Cursor cursor = db.query(ContactModel.ContactEntry.TABLE_NAME, null, null, null, null, null, null, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        List<ContactModel> addedContacts = new ArrayList<ContactModel>();
        while (cursor.moveToNext()) {
            ContactModel contact = new ContactModel();
            contact.setName(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_NAME)));
            contact.setUkId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry._ID))));
            contact.setContactId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID))));
            try {
                contact.setLastContacted(sdf.parse(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_LASTCONTACT))));
                contact.setTtk(sdf.parse(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_TTK))));
                contact.setContactHistory(parseContactHistoryColumn(cursor.getString(cursor.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_CONTACTHISTORY))));

            } catch (Exception e) {
                cursor.close();
            }
            addedContacts.add(contact);
        }

        cursor.close();

        return addedContacts;
    }

    /* for each ContactModel, adds the contact to our db iff they do not exist */
    public long addContact(ContactModel contact) {
        long ret = 0;
        Cursor curs = db.query(ContactModel.ContactEntry.TABLE_NAME, new String[]{ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID}, ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{"" + contact.getContactId()}, null, null, null, null);
        if (curs.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID, contact.getContactId());
            values.put(ContactModel.ContactEntry.COLUMN_NAME_NAME, contact.getName());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long oneMonthMillis = 1000l * 60l * 60l * 24l * 30l;
            Date ttkDate = new Date(oneMonthMillis);
            values.put(ContactModel.ContactEntry.COLUMN_NAME_TTK, dateFormat.format(ttkDate));

            ret = db.insert(ContactModel.ContactEntry.TABLE_NAME, "don't worry about this. no seriously.", values);
        }
        curs.close();
        touchContact(contact);
        return ret;
    }

    public boolean touchContact(ContactModel contact) {
        long ret = 0;
        Cursor curs = db.query(ContactModel.ContactEntry.TABLE_NAME, new String[]{ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID, ContactModel.ContactEntry.COLUMN_NAME_CONTACTHISTORY}, ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()}, null, null, null, null);
        ContentValues values = new ContentValues();

        if (curs.getCount() != 0) {
            curs.moveToFirst();
            List<Date> contactList = parseContactHistoryColumn(curs.getString(curs.getColumnIndex(ContactModel.ContactEntry.COLUMN_NAME_CONTACTHISTORY)));
            contactList.add(new Date());
            // set the format to sql date time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values.put(ContactModel.ContactEntry.COLUMN_NAME_LASTCONTACT, dateFormat.format(new Date()));
            values.put(ContactModel.ContactEntry.COLUMN_NAME_CONTACTHISTORY, getContactHistoryString(contactList));

            ret = db.updateWithOnConflict(ContactModel.ContactEntry.TABLE_NAME, values, ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()}, db.CONFLICT_REPLACE);
        }
        curs.close();

        return (ret > 0);
    }

    /* removeContact() removes from our db; deleteContact() deletes entirely. The ContactModel should have its Android database id.  */
    public int removeContact(ContactModel contact) {
        return db.delete(ContactModel.ContactEntry.TABLE_NAME, ContactModel.ContactEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{""+contact.getContactId()});
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
        finally {
            cursor.close();
        }
        return ret;
    }

    private List<Date> parseContactHistoryColumn(String col) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Date> dates = new ArrayList<Date>();
        if (col == null) {
            return dates;
        }
        String[] strDates = col.split(",");
        try {
            for (String str : strDates) {
                dates.add(dateFormat.parse(str));
            }
        } catch (Exception e) {}

        return dates;
    }

    private String getContactHistoryString(List<Date> dates) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ret = "";
        for (int i = 0; i < dates.size() - 1; i++) {
            ret += dateFormat.format(dates.get(i)) + ",";
        }
        ret += dateFormat.format(dates.get(dates.size() - 1));

        return ret;
    }

}
