package f.myCircle;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.fambam.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeff on 7/19/14.
 */
public class HomeFragment extends ListFragment {

    public HomeFragment() {
    }

    SQLiteDatabase db;
    Activity mActivity;
    String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        Log.v("MainActivity", "onAttach");

        mActivity = activity;
    }

    public void onResume() {
        super.onResume();

        final ListView lv = getListView();

        UkDbHelper helper = new UkDbHelper(mActivity);
        db = helper.getWritableDatabase();

        ArrayAdapter<ContactModel> adapter = new HomeArrayAdapter(mActivity, getModel());

        lv.setAdapter(adapter);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                lv.invalidateViews();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private List<ContactModel> getModel() {
        List<ContactModel> list = new ArrayList<ContactModel>();

        // no sub-selection, no sort order, simply every row
        // projection says we want just the _id and the name column


        // define the columns I want the query to return
        projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Log.v("ukService", "uri=" + uri.toString());

        Cursor cursor = db.query(UkEntryContract.UkEntry.TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int contactId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date lastContacted = null, ttk = null;
            String firstName = cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_FIRSTNAME));
            String lastName  = cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_LASTNAME));

            int ukid = Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry._ID)));
            try {
                lastContacted = sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT)));
                ttk = sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_TTK)));
            } catch (Exception e) {}
            if (lastContacted.getTime() + ttk.getTime() > (new Date()).getTime()) {
                list.add(new ContactModel(firstName, lastName, ukid, contactId, lastContacted, ttk));
            }
        }

        Collections.sort(list, new ContactModelTimeComparator());

        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
