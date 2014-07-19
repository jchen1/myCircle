package f.myCircle;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
public class AddFragment extends ListFragment {
    SQLiteDatabase db;
    Activity mActivity;
    private CursorAdapter mAdapter;
    String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };

    public AddFragment() {
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactModel cm = (ContactModel)parent.getAdapter().getItem(position);
                if (cm.isSelected()) {
                    cm.setSelected(false);
                }
                else {
                    cm.setSelected(true);
                }
                lv.invalidateViews();

            }
        });

        UkDbHelper helper = new UkDbHelper(mActivity);
        db = helper.getWritableDatabase();

        ArrayAdapter<ContactModel> adapter = new ContactArrayAdapter(mActivity, getModel());

        lv.setAdapter(adapter);

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

        Cursor cursor = mActivity.getContentResolver().query(uri, projection, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, null);

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
            String[] nameSplit = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).split(" ");
            String firstName = nameSplit[0], lastName = nameSplit.length > 1 ? nameSplit[1] : "";
            ContactModel item = new ContactModel(firstName, lastName, Integer.parseInt(contactId));
            item.setSelected(false);
            list.add(item);
        }
        cursor.close();

        cursor = db.query(UkEntryContract.UkEntry.TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int contactId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_ENTRY_ID)));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            ContactModel item = null;
            for (ContactModel cm : list) {
                if (contactId == cm.getContactId()) {
                    item = cm;
                    break;
                }
            }
            if (item == null) {
                //contact was deleted, delete
            }
            else {
                int ukid = Integer.parseInt(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry._ID)));
                Date lastContacted = null, ttk = null;
                try {
                    lastContacted = sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_LASTCONTACT)));
                    ttk = sdf.parse(cursor.getString(cursor.getColumnIndex(UkEntryContract.UkEntry.COLUMN_NAME_TTK)));
                } catch (Exception e) {}
                item.setUkId(ukid);
                item.setLastContacted(lastContacted);
                item.setTtk(ttk);
                item.setSelected(ukid != -1);
            }
        }

        Collections.sort(list, new ContactModelNameComparator());
        return list;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        return rootView;
    }


}
