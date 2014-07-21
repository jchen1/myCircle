package f.myCircle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeff on 7/18/14.
 */
public class UkDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Uk.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =

            "CREATE TABLE " + UkEntryContract.UkEntry.TABLE_NAME + " (" +
                    UkEntryContract.UkEntry._ID + " INTEGER PRIMARY KEY," +
                    UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID + INT_TYPE + COMMA_SEP +
                    UkEntryContract.UkEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT + DATE_TYPE + COMMA_SEP +
                    UkEntryContract.UkEntry.COLUMN_NAME_TTK + DATE_TYPE +
            " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UkEntryContract.UkEntry.TABLE_NAME;


    public UkDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
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
}