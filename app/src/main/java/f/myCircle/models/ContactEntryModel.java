package f.myCircle.models;

import android.provider.BaseColumns;

/**
 * Created by jeff on 7/18/14.
 */
public final class ContactEntryModel {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ContactEntryModel() {}

    /* Inner class that defines the table contents */
    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "UkContacts";
        public static final String COLUMN_NAME_ENTRY_ID = "contactid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LASTCONTACT = "lastcontact";
        public static final String COLUMN_NAME_TTK = "timetokill";
        public static final String COLUMN_NAME_CONTACTHISTORY = "contacthistory";
    }
}