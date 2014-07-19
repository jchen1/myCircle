package f.myCircle;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
        Log.v("MainActivity", "onAttach");

        mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("MainActivity", "onActivityCreated");

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("MainActivity", "You clicked item " + id + " at position " + position);
            }
        });

        UkDbHelper helper = new UkDbHelper(mActivity);
        db = helper.getWritableDatabase();

        ArrayAdapter<ContactModel> adapter = new ContactArrayAdapter(mActivity, getModel());

        lv.setAdapter(adapter);
/*
            mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_add_item, null, FROM_COLUMNS, TO_IDS, 0);
            lv.setAdapter(mAdapter);
            getLoaderManager().initLoader(0, null, this);*/
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

        return list;
    }

        /*
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            // load from the "Contacts table"
            Uri contentUri = ContactsContract.Contacts.CONTENT_URI;
            Log.v("MainActivity", "onCreateLoader");

            // no sub-selection, no sort order, simply every row
            // projection says we want just the _id and the name column
            CursorLoader allContacts = new CursorLoader(getActivity(),
                    contentUri,
                    projection,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",//null,
                    null,
                    null);


        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v("MainActivity", "onLoadFinished");
            Log.v("MainActivity", "" + data.getCount());

            // Once cursor is loaded, give it to adapter
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            Log.v("MainActivity", "onLoaderReset");

            // on reset take any old cursor away
            mAdapter.swapCursor(null);
        }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        return rootView;
    }

        /*
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Context context = getActivity();
            int layout = android.R.layout.simple_list_item_1;
            Cursor c = null; // there is no cursor yet
            int flags = 0; // no auto-requery! Loader requeries.
            mAdapter = new SimpleCursorAdapter(context, layout, c, FROM, TO, flags);
        }

        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContactsList = (ListView) getActivity().findViewById(R.layout.fragment_add);
            mCursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.contact_list_item,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0);
            mContactList.setAdapter(mCursorAdapter);
        }

        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = getActivity();
            UkDbHelper helper = new UkDbHelper(mActivity);
            db = helper.getWritableDatabase();

            // each time we are started use our listadapter
            setListAdapter(mAdapter);
            // and tell loader manager to start loading
            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_addcontacts_list, container, false);
            return rootView;
        }

        // and name should be displayed in the text1 textview in item layout
        private static final String[] FROM = { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };
        private static final int[] TO = { android.R.id.text1 };
        // columns requested from the database
        private static final String[] PROJECTION = {
                ContactsContract.Contacts._ID, // _ID is always required
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY // that's what we want to display
        }; */

}
