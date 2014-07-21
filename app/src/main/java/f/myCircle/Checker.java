package f.myCircle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.List;

/**
 * Created by jeff on 7/19/14.
 */
public class Checker implements Runnable {
    SQLiteDatabase db;
    DatabaseManager dbMgr;
    Context ctx;

    public Checker(SQLiteDatabase _db, Context _ctx) {
        db = _db;
        ctx = _ctx;
        dbMgr = new DatabaseManager(db, ctx);
    }

    @Override
    public void run() {
        List<ContactModel> addedContacts = dbMgr.getAddedContacts();

        for (ContactModel contact : addedContacts) {
            if ((new Date()).getTime() > contact.getLastContacted().getTime()+contact.getTtk().getTime()){
                dbMgr.deleteContact(contact);
            }
        }
    }
}

